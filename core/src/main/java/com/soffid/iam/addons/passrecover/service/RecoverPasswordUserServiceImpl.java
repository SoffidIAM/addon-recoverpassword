/**
 * 
 */
package com.soffid.iam.addons.passrecover.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.http.HttpStatus;

import com.soffid.iam.ServiceLocator;
import com.soffid.iam.addons.rememberPassword.common.MissconfiguredRecoverException;
import com.soffid.iam.addons.rememberPassword.common.RecoverMethodEnum;
import com.soffid.iam.addons.rememberPassword.common.RememberPassConfig;
import com.soffid.iam.addons.rememberPassword.common.RememberPasswordChallenge;
import com.soffid.iam.addons.rememberPassword.common.UserAnswer;
import com.soffid.iam.api.Account;
import com.soffid.iam.api.Audit;
import com.soffid.iam.api.Challenge;
import com.soffid.iam.api.PasswordValidation;
import com.soffid.iam.api.PolicyCheckResult;
import com.soffid.iam.api.SoffidObjectType;
import com.soffid.iam.api.User;
import com.soffid.iam.api.UserAccount;
import com.soffid.iam.model.AccountEntity;
import com.soffid.iam.model.AuditEntity;
import com.soffid.iam.model.PasswordDomainEntity;
import com.soffid.iam.model.ServerEntity;
import com.soffid.iam.model.UserAccountEntity;
import com.soffid.iam.model.UserDataEntity;
import com.soffid.iam.model.UserEntity;
import com.soffid.iam.remote.RemoteServiceLocator;
import com.soffid.iam.sync.engine.intf.DebugTaskResults;
import com.soffid.iam.sync.service.SyncStatusService;
import com.soffid.iam.utils.Security;

import es.caib.seycon.ng.comu.ServerType;
import es.caib.seycon.ng.exception.BadPasswordException;
import es.caib.seycon.ng.exception.InternalErrorException;
import es.caib.seycon.ng.exception.UnknownUserException;

/**
 * @author (C) Soffid 2014
 * 
 */
public class RecoverPasswordUserServiceImpl extends
		RecoverPasswordUserServiceBase {

	HashMap<Long, RecoverPasswordChallenge> challenges = new HashMap<Long, RecoverPasswordChallenge>();
	long nextPurge = 0;
	org.apache.commons.logging.Log log = LogFactory.getLog(getClass());

	private void purge() {
		synchronized (challenges) {
			Date now = new Date();
			for (Iterator<Long> it = challenges.keySet().iterator(); it
					.hasNext();) {
				Long id = it.next();
				RecoverPasswordChallenge ch = challenges.get(id);
				if (ch.getExpirationDate().getTime().before(now)) {
					it.remove();
				}
			}
		}
		nextPurge = System.currentTimeMillis() + 120000; // every 2 minutes
	}

	private RecoverPasswordChallenge getStoredChallenge(
			RecoverPasswordChallenge vo) {
		if (System.currentTimeMillis() > nextPurge)
			purge();
		return challenges.get(vo.getChallengId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.soffid.iam.addons.rememberPassword.service.
	 * RememberPasswordUserServiceBase
	 * #handleResponseChallenge(com.soffid.iam.addons
	 * .rememberPassword.common.RememberPasswordChallenge)
	 */
	@Override
	protected boolean handleResponseChallenge(
			RecoverPasswordChallenge challenge) throws Exception {
		Security.nestedLogin(Security.ALL_PERMISSIONS);
		try {
			RecoverPasswordChallenge stored = getStoredChallenge(challenge);
      if (stored==null)
			  return false;
      
			if (stored.getMethod() == RecoverMethodEnum.RECOVER_BY_OTP) {
				UserAnswer answer = challenge.getQuestions().iterator().next();
				if (getOTPValidationService().validatePin(stored.getOtpChallenge(), answer.getAnswer())) {
					stored.setAnswered(true);
					return true;
				}
				else
					return false;
			} else {
				for (UserAnswer answer : stored.getQuestions()) {
					for (UserAnswer answer2 : challenge.getQuestions()) {
						if (answer.getQuestion().replaceAll("\\?",  "").equals(answer2.getQuestion().replaceAll("\\?",  ""))) {
							answer.setAnswer(answer2.getAnswer());
							break;
						}
					}
				}
				
				// Verify answers
				if (verifyAnswers(stored)) {
					audit (stored.getUser(), null, "SC_RPANSW", "S", null); //$NON-NLS-1$ //$NON-NLS-2$
					stored.setAnswered(true);
					return true;
				} else
					return false;
			}
	
		} finally {
			Security.nestedLogoff();
		}
	}

	/**
	 * @param stored
	 * @return
	 * @throws InternalErrorException
	 */
	private boolean verifyAnswers(RecoverPasswordChallenge stored)
			throws InternalErrorException {
		int answeredOK = 0; // Question answered correctly
		int toAnswer; // Quenstions to answser
		boolean verified = true; // Answers verified


		Collection<UserAnswer> userAnswers ;
		if (stored.getMethod().equals (RecoverMethodEnum.RECOVER_BY_MAIL) ||
				stored.getMethod().equals (RecoverMethodEnum.RECOVER_BY_SMS))
		{
			toAnswer = 1;
			userAnswers = new LinkedList<UserAnswer>();
			for (UserAnswer answer: stored.getQuestions())
			{
				UserAnswer answer2 = new UserAnswer(answer);
				answer2.setAnswer(stored.getEmailPin());
				userAnswers.add(answer2);
			}
		}
		else
		{
			toAnswer = getRecoverPasswordService().getRecoverPassConfiguration().getRight();
			userAnswers = getRecoverPasswordService()
				.findUserAnswersByUserName(stored.getUser());
		}
		
		log.info("Testing answers ");

		for (UserAnswer userAnswer : userAnswers) {
			log.info("Testing answer "+userAnswer.getQuestion());
			if (userAnswer.getAnswer() != null)
			{
				for (UserAnswer storedAnswer : stored.getQuestions()) {
//					log.info("Stored "+storedAnswer.getQuestion()+"=>"+storedAnswer.getAnswer());
					if (storedAnswer.getQuestion() != null &&
							storedAnswer.getAnswer() != null &&
							storedAnswer.getQuestion().replaceAll("\\?",  "").
								equalsIgnoreCase(userAnswer.getQuestion().replaceAll("\\?",  "")))
					{
						String q1 = userAnswer.getAnswer().replaceAll(" *", "").toLowerCase(); //$NON-NLS-1$ //$NON-NLS-2$
						String q2 = storedAnswer.getAnswer().replaceAll(" *", "").toLowerCase(); //$NON-NLS-1$ //$NON-NLS-2$
						if (q1.equals(q2)) {
							answeredOK++;
							log.info("Answered correctly "+storedAnswer.getQuestion()+" ("+answeredOK+")");
						} else {
							audit (stored.getUser(), storedAnswer.getQuestion(), "SC_RPANSW", "F", null); //$NON-NLS-1$ //$NON-NLS-2$
						}
					} else {
						log.info("Skip answer "+storedAnswer.getQuestion());
					}
				}
			}
		}

		// Check required questions answered
		if (answeredOK < toAnswer)
			verified = false;

		return verified;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.soffid.iam.addons.rememberPassword.service.
	 * RememberPasswordUserServiceBase
	 * #handleResetPassword(com.soffid.iam.addons.
	 * rememberPassword.common.RememberPasswordChallenge)
	 */
	@Override
	protected void handleResetPassword(RecoverPasswordChallenge challenge)
			throws Exception {
		Security.nestedLogin(Security.ALL_PERMISSIONS);
		try {
			RecoverPasswordChallenge stored = getStoredChallenge(challenge);
			if (!stored.isAnswered())
				throw new InternalErrorException(Messages.getString("RememberPasswordUserServiceImpl.NoAnswerError")); //$NON-NLS-1$
			String dispatcher = stored.getDispatcher();
			if (dispatcher == null)
				dispatcher = getInternalPasswordService().getDefaultDispatcher();
			PasswordDomainEntity passwordDomain = getSystemEntityDao()
					.findByName(dispatcher).getPasswordDomain();
			audit (stored.getUser(), null, "SC_USUARI", "p", passwordDomain.getName()); //$NON-NLS-1$ //$NON-NLS-2$
			UserEntity user = getUserEntityDao().findByUserName(stored.getUser());
			
			RecoverPassConfig config = getRecoverPasswordService().getRecoverPassConfiguration();
			if (config.isAllowPasswordReuse() && 
					getInternalPasswordService().checkPassword(user, passwordDomain, challenge.getPassword(), true, false) == PasswordValidation.PASSWORD_GOOD)
			{ 
				for ( UserAccountEntity ua: user.getAccounts())
				{
					AccountEntity account = ua.getAccount();
					if (! account.isDisabled() && "S".equals(account.getSystem().getTrusted()) &&
							account.getSystem().getUrl() != null)
						synchronizeAccount (account);
				}
			} else {
				PolicyCheckResult verify = getInternalPasswordService().checkPolicy(user, passwordDomain, challenge.getPassword());
				if (! verify.isValid())
					throw new BadPasswordException(verify.getReason());
				
				getInternalPasswordService().storeAndSynchronizePassword(user,
						passwordDomain, challenge.getPassword(), false);
			}
			synchronized (challenges) {
				challenges.remove(challenge.getChallengId());
			}
		} finally {
			Security.nestedLogoff();
		}
	}

	private void synchronizeAccount(AccountEntity account) throws Exception {
		Exception lastException = null;

		for (ServerEntity se : getServerEntityDao().loadAll()) {
            if (se.getType().equals(ServerType.MASTERSERVER)) {
            	SyncStatusService sss = null;
                try {
                    RemoteServiceLocator rsl = new com.soffid.iam.remote.RemoteServiceLocator(se.getName());
                    rsl.setAuthToken(se.getAuth());
                    sss = rsl.getSyncStatusService();
                } catch (Exception e) {
                    lastException = e;
                }
                if (sss != null)
                {
                	DebugTaskResults r = sss.testPropagateObject(account.getSystem().getName(), 
                			SoffidObjectType.OBJECT_ACCOUNT, 
                			account.getName(), null);
                	if (r != null && r.getException() != null)
                		throw r.getException();
                	return;
                }
            }
        }
		if (lastException != null)
			throw lastException;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.soffid.iam.addons.rememberPassword.service.
	 * RememberPasswordUserServiceBase#handleRequestChallenge(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	protected RecoverPasswordChallenge handleRequestChallenge(String account,
			String dispatcher) throws Exception {
		if (dispatcher == null || dispatcher.trim().length() == 0)
			dispatcher = getInternalPasswordService().getDefaultDispatcher();
		
		Security.nestedLogin(Security.ALL_PERMISSIONS);
		try {
			Account acc = getAccountService().findAccount(account, dispatcher);
			if (acc == null)
				throw new UnknownUserException(account+" at "+dispatcher);
			if (! (acc instanceof UserAccount))
			{
				throw new UnknownUserException(account+" at "+dispatcher);
			}
			String user = ( (UserAccount) acc ).getUser();
			
			return handleRequestChallenge (user);
		} finally {
			Security.nestedLogoff();
		}
		 
	}

	private RecoverMethodEnum guessRecoveryMethod(String user, RecoverPasswordChallenge challenge) throws InternalErrorException {
		RecoverPassConfig config = getRecoverPasswordService().getRecoverPassConfiguration();
		
		if (config.getPreferredMethod().equals (RecoverMethodEnum.RECOVER_BY_MAIL))
		{
//			log.info("Preferred email");
			if (hasRecoveryEmail (user) && config.isAllowMailRecovery())
				return RecoverMethodEnum.RECOVER_BY_MAIL;
			else if (hasQuestions (user) && config.isAllowQuestionRecovery())
				return RecoverMethodEnum.RECOVER_BY_QUESTIONS;
			else if (config.isAllowSmsRecovery() && hasSms(user, config))
				return RecoverMethodEnum.RECOVER_BY_SMS;
			else if (config.isAllowOtpRecovery() && hasToken(user, challenge))
				return RecoverMethodEnum.RECOVER_BY_OTP;
			else
				return null;
		}
		else if (config.getPreferredMethod().equals (RecoverMethodEnum.RECOVER_BY_QUESTIONS))
		{
//			log.info("Preferred questions");
			if (hasQuestions (user) && config.isAllowQuestionRecovery())
				return RecoverMethodEnum.RECOVER_BY_QUESTIONS;
			else if (hasRecoveryEmail (user) && config.isAllowMailRecovery())
				return RecoverMethodEnum.RECOVER_BY_MAIL;
			else if (config.isAllowSmsRecovery() && hasSms(user, config))
				return RecoverMethodEnum.RECOVER_BY_SMS;
			else if (config.isAllowOtpRecovery() && hasToken(user, challenge))
				return RecoverMethodEnum.RECOVER_BY_OTP;
			else
				return null;
		}
		else if (config.getPreferredMethod().equals (RecoverMethodEnum.RECOVER_BY_SMS))
		{
//			log.info("Preferred sms");
			if (config.isAllowSmsRecovery() && hasSms(user, config))
				return RecoverMethodEnum.RECOVER_BY_SMS;
			else if (hasQuestions (user) && config.isAllowQuestionRecovery())
				return RecoverMethodEnum.RECOVER_BY_QUESTIONS;
			else if (hasRecoveryEmail (user) && config.isAllowMailRecovery())
				return RecoverMethodEnum.RECOVER_BY_MAIL;
			else if (config.isAllowOtpRecovery() && hasToken(user, challenge))
				return RecoverMethodEnum.RECOVER_BY_OTP;
			else
				return null;
		}
		else
		{
//			log.info("Preferred otp");
			if (config.isAllowOtpRecovery() && hasToken(user, challenge))
				return RecoverMethodEnum.RECOVER_BY_OTP;
			else if (hasQuestions (user) && config.isAllowQuestionRecovery())
				return RecoverMethodEnum.RECOVER_BY_QUESTIONS;
			else if (hasRecoveryEmail (user) && config.isAllowMailRecovery())
				return RecoverMethodEnum.RECOVER_BY_MAIL;
			else if (config.isAllowSmsRecovery() && hasSms(user, config))
				return RecoverMethodEnum.RECOVER_BY_SMS;
			else
				return null;
		}
			
	}
	
	private boolean hasRecoveryEmail(String user) {
		String email = getRecoveryEmail (user);
		return email != null && !email.trim().isEmpty();
	}

	private boolean hasToken(String user, RememberPasswordChallenge challenge) throws InternalErrorException {
		Challenge ch = new Challenge();
		ch.setUser(getUserService().findUserByUserName(user));
		Challenge token = getOTPValidationService().selectToken(ch);
		challenge.setOtpChallenge(token);
		return token.getCardNumber() != null;
	}

	private boolean hasSms(String user, RememberPassConfig config) {
		if (config.getSmsAttribute() == null)
			return false;
		for (UserDataEntity att: getUserDataEntityDao().findByDataType(user, config.getSmsAttribute())) {
			String v = att.getValue();
			if (v != null && !v.trim().isEmpty())
				return true;
		}
		return false;
	}


	private String getRecoveryEmail(String user) {
		UserEntity userEntity = getUserEntityDao().findByUserName(user);
		if (userEntity == null)
			return null;
		for (UserDataEntity dus: userEntity.getUserData())
		{
			if (dus.getValue() != null && dus.getDataType().getName().equalsIgnoreCase("EMAIL")) //$NON-NLS-1$
				return dus.getValue();
		}
		if (userEntity.getShortName() != null && userEntity.getMailDomain() != null)
			return userEntity.getShortName()+"@"+userEntity.getMailDomain().getName(); //$NON-NLS-1$
		
		return null;
	}

	private boolean hasQuestions(String user) throws InternalErrorException {
		int request = getRecoverPasswordService()
				.getRecoverPassConfiguration().getNumber();
		List<UserAnswer> userAnswers = (List<UserAnswer>) getRecoverPasswordService()
				.findUserAnswersByUserName(user);
		if (request == 0)
			return false;
		
		for (UserAnswer answer: userAnswers)
		{
			if (answer.getAnswer() != null && answer.getAnswer().trim().length() > 0)
			{
				request --;
				if (request == 0)
					return true;
			}
		}
		return false;
	}

	/**
	 * @param user
	 * @return
	 * @throws InternalErrorException 
	 */
	private Collection<UserAnswer> generateQuestions(String user) throws InternalErrorException {
		Random rand = new Random();
		int request = getRecoverPasswordService()
				.getRecoverPassConfiguration().getQuestions();

		List<UserAnswer> userAnswers = (List<UserAnswer>) getRecoverPasswordService()
				.findUserAnswersByUserName(user);

		if (!checkAnswers(userAnswers, request))
			throw new InternalErrorException(
					Messages.getString("RememberPasswordUserServiceImpl.UserQuestionsError")); //$NON-NLS-1$

		Collection<UserAnswer> requestQuestions = new LinkedList<UserAnswer>();

		while (requestQuestions.size() < request) {
			if (userAnswers.isEmpty())
				throw new InternalErrorException(
						Messages.getString("RememberPasswordUserServiceImpl.UserQuestionsError")); //$NON-NLS-1$
			int questionIndex = rand.nextInt(userAnswers.size());

			if (checkUserAnswer(userAnswers.get(questionIndex)))
				requestQuestions.add(userAnswers.get(questionIndex));

			userAnswers.remove(questionIndex);
		}

		return requestQuestions;
	}

	/**
	 * @param user
	 * @return
	 * @throws InternalErrorException 
	 */
	private Collection<UserAnswer> generateEmail(RecoverPasswordChallenge challenge, String user) throws InternalErrorException {
		Random rand = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < 4; i++)
		{
			if (sb.length() > 0 )
				sb.append (' ');
			for (int j = 0; j < 4 ; j++)
				sb.append ( (char) ('0'+rand.nextInt(10)));
		}
		Collection<UserAnswer> requestQuestions = new LinkedList<UserAnswer>();

		UserAnswer answer = new UserAnswer();
		answer.setId(new Long(0));
		answer.setQuestion(Messages.getString("RememberPasswordUserServiceImpl.EmailQuestion")); //$NON-NLS-1$
		answer.setUser(user);
		answer.setAnswer(sb.toString());
		requestQuestions.add(answer);
		
		challenge.setEmailPin(sb.toString());

		String msg = String.format ( Messages.getString("RememberPasswordUserServiceImpl.Email.message"), //$NON-NLS-1$
				user, sb.toString());
		getMailService().sendHtmlMail(getRecoveryEmail(user), Messages.getString("RememberPasswordUserServiceImpl.5"), msg); //$NON-NLS-1$
		return requestQuestions;
	}

	private Collection<UserAnswer> generateSms(RememberPasswordChallenge challenge, String user) throws InternalErrorException, IOException {
		Random rand = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < 8; i++)
		{
			sb.append ( (char) ('0'+rand.nextInt(10)));
		}
		Collection<UserAnswer> requestQuestions = new LinkedList<UserAnswer>();

		UserAnswer answer = new UserAnswer();
		answer.setId(new Long(0));
		answer.setQuestion(Messages.getString("RememberPasswordUserServiceImpl.SMSQuestion")); //$NON-NLS-1$
		answer.setUser(user);
		answer.setAnswer(sb.toString());
		requestQuestions.add(answer);
		
		challenge.setEmailPin(sb.toString());
		challenge.setUser(user);
		
		RememberPassConfig cfg = getRememberPasswordService().getRememberPassConfiguration();
		WebClient request = WebClient.create(translate(cfg.getSmsUrl(), challenge));
		if (cfg.getSmsHeaders() != null) {
			for (String line: cfg.getSmsHeaders().split("\n")) {
				line = line.trim();
				if ( !line.isEmpty()) {
					int i = line.indexOf(':');
					String tag = line.substring(0,i).trim();
					String value = line.substring(i+1).trim();
					if (!tag.isEmpty() && !value.isEmpty()) {
						request.header(tag, value);
					}
				}
			}
		}
		log.info("Sending message to "+challenge.getUser());
		Response response = request.invoke(cfg.getSmsMethod(), translate(cfg.getSmsBody(), challenge));

		if ( response.getStatus() != HttpStatus.SC_OK)
			throw new InternalError ("Error sending SMS message: HTTP/"+response.getStatus());
		InputStream in = (InputStream) response.getEntity();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int read;
		while ((read = in.read()) >= 0)
			out.write(read);
		log.info("SMS gateway response: "+out.toString("UTF-8"));
		if ( cfg.getSmsResponseToCheck() != null) {
			if (!out.toString("UTF-8").contains(cfg.getSmsResponseToCheck()))
				throw new InternalErrorException("Cannot send SMS message");
		}
		return requestQuestions;
		
	}

	private String translate(String smsBody, RememberPasswordChallenge challenge) throws UnsupportedEncodingException {
		StringBuffer b = new StringBuffer();
		int pos = 0;
		do {
			int next = smsBody.indexOf("${", pos);
			if (next < 0) { 
				b.append(smsBody.substring(pos));
				break;
			} else {
				int last = smsBody.indexOf("}", pos);
				if (last < 0) {
					b.append(smsBody.substring(pos));
					break;
				}
				String tag = smsBody.substring(next+2, last);
				Object value = eval (tag, challenge);
				b.append(smsBody.substring(pos, next));
				if (value != null) 
					b.append( URLEncoder.encode(value.toString(), "UTF-8"));
				pos = last + 1;
			}
		} while (true);
		return b.toString();
	}


	private Object eval(String tag, RememberPasswordChallenge challenge) {
		Object value = null;
		
		if (tag.equals("PIN"))
			return challenge.getEmailPin();
		Collection<UserDataEntity> list = getUserDataEntityDao().findByDataType(challenge.getUser(), tag);
		if (list.isEmpty()) {
			UserEntity userEntity = getUserEntityDao().findByUserName(challenge.getUser());
			if (userEntity != null) {
				User user = getUserEntityDao().toUser(userEntity);
				try {
					value = PropertyUtils.getProperty(user, tag);
				} catch (Exception e) {
				}
			}
		} else {
			value = list.iterator().next().getObjectValue();
		}
		return value;
	}

	private Collection<UserAnswer> generateOtp(RememberPasswordChallenge challenge, String user) throws InternalErrorException, UnsupportedEncodingException {
		Random rand = new Random();
		Collection<UserAnswer> requestQuestions = new LinkedList<UserAnswer>();

		UserAnswer answer = new UserAnswer();
		answer.setId(new Long(0));
		answer.setQuestion(Messages.getString("RememberPasswordUserServiceImpl.OTPQuestion")+challenge.getOtpChallenge().getCardNumber() ); //$NON-NLS-1$
		answer.setUser(user);
		answer.setAnswer(user);
		requestQuestions.add(answer);
		
		return requestQuestions;
	}

	/**
	 * @param userAnswers
	 * @param request 
	 * @return
	 */
	private boolean checkAnswers(List<UserAnswer> userAnswers, int request) {
		int validAnswers = 0;

		for (UserAnswer userAnswer : userAnswers) {
			if ((userAnswer.getAnswer() != null)
					&& !userAnswer.getAnswer().isEmpty())
				validAnswers++;
		}

		// Check valid user answers
		if (validAnswers < request)
			return false;

		else
			return true;
	}

	/**
	 * @param userAnswer
	 * @return
	 */
	private boolean checkUserAnswer(UserAnswer userAnswer) {
		// Check valid answer
		if ((userAnswer.getAnswer()!=null) && !userAnswer.getAnswer().isEmpty())
			return true;
		
		else
			return false;
	}

	private void audit(String auditedUser, String question, String object, String action, String passwordDomain) {
		Audit auditoria = new Audit();
		auditoria.setAction(action); //$NON-NLS-1$
		auditoria.setUser(auditedUser);
		auditoria.setAuthor(auditedUser);
		auditoria.setPasswordDomain(passwordDomain);
		auditoria.setCalendar(Calendar.getInstance());
		auditoria.setObject(object); //$NON-NLS-1$
		auditoria.setMessage(question);

		AuditEntity auditoriaEntity = getAuditEntityDao()
				.auditToEntity(auditoria);
		getAuditEntityDao().create(auditoriaEntity);
	}

	@Override
	protected RecoverPasswordChallenge handleRequestChallenge(String user)
			throws Exception {
		Security.nestedLogin(Security.ALL_PERMISSIONS);
		try {
			RecoverPasswordChallenge challenge = new RecoverPasswordChallenge();
	
			challenge.setMethod(guessRecoveryMethod (user, challenge));
			
			if (challenge.getMethod() == null)
				throw new MissconfiguredRecoverException(
						Messages.getString("RememberPasswordUserServiceImpl.UserQuestionsError")); //$NON-NLS-1$
	
			int request;
			int right;
			if (challenge.getMethod().equals(RecoverMethodEnum.RECOVER_BY_QUESTIONS))
			{
				request = getRecoverPasswordService()
						.getRecoverPassConfiguration().getQuestions();
				right = getRecoverPasswordService()
						.getRecoverPassConfiguration().getRight();
				if (request <= 0 || right <= 0)
					throw new InternalErrorException (Messages.getString("RememberPasswordUserServiceImpl.disabledFeature")); //$NON-NLS-1$
			}
			else
			{
				request = right = 1;
			}
		
			// Check existing user
			if (getUserEntityDao().findByUserName(user) != null) {
				Calendar date = Calendar.getInstance();
	
				challenge.setChallengeDate(date);
	
				date.add(Calendar.MINUTE, 30);
				challenge.setExpirationDate(date);
				if (challenge.getMethod().equals(RecoverMethodEnum.RECOVER_BY_QUESTIONS))
					challenge.setQuestions(generateQuestions(user));
				else if (challenge.getMethod().equals(RecoverMethodEnum.RECOVER_BY_MAIL))
					challenge.setQuestions(generateEmail(challenge, user));
				else if (challenge.getMethod().equals(RecoverMethodEnum.RECOVER_BY_SMS))
					challenge.setQuestions(generateSms(challenge, user));
				else if (challenge.getMethod().equals(RecoverMethodEnum.RECOVER_BY_OTP))
					challenge.setQuestions(generateOtp(challenge, user));
				challenge.setUser(user);
				challenge.setChallengId(new Long(challenge.hashCode()));
				synchronized (challenges) {
					challenges.put(challenge.getChallengId(), challenge);
				}
			} else {
				throw new UnknownUserException(Messages.getString("RememberPasswordUserServiceImpl.UserNotFoundError")); //$NON-NLS-1$
			}
	
			RecoverPasswordChallenge challenge2 = new RecoverPasswordChallenge(challenge);
			challenge2.setQuestions( new LinkedList<UserAnswer>( ));
			for (UserAnswer u: challenge.getQuestions())
			{
				UserAnswer u2 = new UserAnswer(u);
				u2.setAnswer(null);
				challenge2.getQuestions().add(u2);
			}
			challenge2.setEmailPin(null);
			audit (challenge2.getUser(), null, "SC_RPANSW", "R", null); //$NON-NLS-1$ //$NON-NLS-2$
			return challenge2;
		} finally {
			Security.nestedLogoff();
		}
	}

}
