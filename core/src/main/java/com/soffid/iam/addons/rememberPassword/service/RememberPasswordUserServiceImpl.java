/**
 * 
 */
package com.soffid.iam.addons.rememberPassword.service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.soffid.iam.addons.rememberPassword.common.RecoverMethodEnum;
import com.soffid.iam.addons.rememberPassword.common.RememberPassConfig;
import com.soffid.iam.addons.rememberPassword.common.RememberPasswordChallenge;
import com.soffid.iam.addons.rememberPassword.common.UserAnswer;

import es.caib.bpm.mail.EmailConfig;
import es.caib.seycon.ng.comu.Account;
import es.caib.seycon.ng.comu.Auditoria;
import es.caib.seycon.ng.comu.PolicyCheckResult;
import es.caib.seycon.ng.comu.UserAccount;
import es.caib.seycon.ng.exception.BadPasswordException;
import es.caib.seycon.ng.exception.InternalErrorException;
import es.caib.seycon.ng.exception.UnknownUserException;
import es.caib.seycon.ng.model.AuditoriaEntity;
import es.caib.seycon.ng.model.DadaUsuariEntity;
import es.caib.seycon.ng.model.DominiContrasenyaEntity;
import es.caib.seycon.ng.model.UsuariEntity;
import es.caib.seycon.ng.utils.MailUtils;

/**
 * @author (C) Soffid 2014
 * 
 */
public class RememberPasswordUserServiceImpl extends
		RememberPasswordUserServiceBase {

	HashMap<Long, RememberPasswordChallenge> challenges = new HashMap<Long, RememberPasswordChallenge>();
	long nextPurge = 0;

	private void purge() {
		synchronized (challenges) {
			Date now = new Date();
			for (Iterator<Long> it = challenges.keySet().iterator(); it
					.hasNext();) {
				Long id = it.next();
				RememberPasswordChallenge ch = challenges.get(id);
				if (ch.getExpirationDate().getTime().before(now)) {
					it.remove();
				}
			}
		}
		nextPurge = System.currentTimeMillis() + 120000; // every 2 minutes
	}

	private RememberPasswordChallenge getStoredChallenge(
			RememberPasswordChallenge vo) {
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
			RememberPasswordChallenge challenge) throws Exception {
		RememberPasswordChallenge stored = getStoredChallenge(challenge);
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

	/**
	 * @param stored
	 * @return
	 * @throws InternalErrorException
	 */
	private boolean verifyAnswers(RememberPasswordChallenge stored)
			throws InternalErrorException {
		int answeredOK = 0; // Question answered correctly
		int toAnswer; // Quenstions to answser
		boolean verified = true; // Answers verified


		Collection<UserAnswer> userAnswers ;
		if (stored.getMethod().equals (RecoverMethodEnum.RECOVER_BY_MAIL))
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
			toAnswer = getRememberPasswordService().getRememberPassConfiguration().getRight();
			userAnswers = getRememberPasswordService()
				.findUserAnswersByUserName(stored.getUser());
		}
		
		for (UserAnswer userAnswer : userAnswers) {
			if (userAnswer.getAnswer() != null)
			{
				for (UserAnswer storedAnswer : stored.getQuestions()) {
					if (storedAnswer.getQuestion() != null &&
							storedAnswer.getQuestion().replaceAll("\\?",  "").
								equals(userAnswer.getQuestion().replaceAll("\\?",  "")) && 
							userAnswer.getAnswer() != null)
					{
						String q1 = userAnswer.getAnswer().replaceAll(" *", "").toLowerCase(); //$NON-NLS-1$ //$NON-NLS-2$
						String q2 = storedAnswer.getAnswer().replaceAll(" *", "").toLowerCase(); //$NON-NLS-1$ //$NON-NLS-2$
						if (q1.equals(q2)) {
							answeredOK++;
						} else {
							audit (stored.getUser(), storedAnswer.getQuestion(), "SC_RPANSW", "F", null); //$NON-NLS-1$ //$NON-NLS-2$
						}
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
	protected void handleResetPassword(RememberPasswordChallenge challenge)
			throws Exception {
		RememberPasswordChallenge stored = getStoredChallenge(challenge);
		if (!stored.isAnswered())
			throw new InternalErrorException(Messages.getString("RememberPasswordUserServiceImpl.NoAnswerError")); //$NON-NLS-1$
		String dispatcher = stored.getDispatcher();
		if (dispatcher == null)
			dispatcher = getInternalPasswordService().getDefaultDispatcher();
		DominiContrasenyaEntity passwordDomain = getDispatcherEntityDao()
				.findByCodi(dispatcher).getDomini();
		audit (stored.getUser(), null, "SC_USUARI", "p", passwordDomain.getCodi()); //$NON-NLS-1$ //$NON-NLS-2$
		UsuariEntity user = getUsuariEntityDao().findByCodi(stored.getUser());
		PolicyCheckResult verify = getInternalPasswordService().checkPolicy(user, passwordDomain, challenge.getPassword());
		if (! verify.isValid())
			throw new BadPasswordException(verify.getReason());
		
		getInternalPasswordService().storeAndSynchronizePassword(user,
				passwordDomain, challenge.getPassword(), false);
		synchronized (challenges) {
			challenges.remove(challenge.getChallengId());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.soffid.iam.addons.rememberPassword.service.
	 * RememberPasswordUserServiceBase#handleRequestChallenge(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	protected RememberPasswordChallenge handleRequestChallenge(String account,
			String dispatcher) throws Exception {
		if (dispatcher == null || dispatcher.trim().length() == 0)
			dispatcher = getInternalPasswordService().getDefaultDispatcher();
		
		Account acc = getAccountService().findAccount(account, dispatcher);
		if (acc == null)
			throw new UnknownUserException(account+" at "+dispatcher);
		if (! (acc instanceof UserAccount))
		{
			throw new UnknownUserException(account+" at "+dispatcher);
		}
		String user = ( (UserAccount) acc ).getUser();
		
		return handleRequestChallenge (user);
	}

	private RecoverMethodEnum guessRecoveryMethod(String user) throws InternalErrorException {
		RememberPassConfig config = getRememberPasswordService().getRememberPassConfiguration();
		
		if (config.getPreferredMethod().equals (RecoverMethodEnum.RECOVER_BY_MAIL))
		{
			if (hasRecoveryEmail (user) && config.isAllowMailRecovery())
				return RecoverMethodEnum.RECOVER_BY_MAIL;
			else if (hasQuestions (user) && config.isAllowQuestionRecovery())
				return RecoverMethodEnum.RECOVER_BY_QUESTIONS;
			else
				return null;
		}
		else
		{
			if (hasQuestions (user) && config.isAllowQuestionRecovery())
				return RecoverMethodEnum.RECOVER_BY_QUESTIONS;
			else if (hasRecoveryEmail (user) && config.isAllowMailRecovery())
				return RecoverMethodEnum.RECOVER_BY_MAIL;
			else
				return null;
		}
			
	}

	private boolean hasRecoveryEmail(String user) {
		return getRecoveryEmail (user) != null;
	}

	private String getRecoveryEmail(String user) {
		UsuariEntity userEntity = getUsuariEntityDao().findByCodi(user);
		if (userEntity == null)
			return null;
		for (DadaUsuariEntity dus: userEntity.getDadaUsuari())
		{
			if (dus.getValorDada() != null && dus.getTipusDada().getCodi().equalsIgnoreCase("EMAIL")) //$NON-NLS-1$
				return dus.getValorDada();
		}
		if (userEntity.getNomCurt() != null && userEntity.getDominiCorreu() != null)
			return userEntity.getNomCurt()+"@"+userEntity.getDominiCorreu().getCodi(); //$NON-NLS-1$
		
		return null;
	}

	private boolean hasQuestions(String user) throws InternalErrorException {
		int request = getRememberPasswordService()
				.getRememberPassConfiguration().getNumber();
		List<UserAnswer> userAnswers = (List<UserAnswer>) getRememberPasswordService()
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
		int request = getRememberPasswordService()
				.getRememberPassConfiguration().getQuestions();

		List<UserAnswer> userAnswers = (List<UserAnswer>) getRememberPasswordService()
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
	private Collection<UserAnswer> generateEmail(RememberPasswordChallenge challenge, String user) throws InternalErrorException {
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
		Auditoria auditoria = new Auditoria();
		auditoria.setAccio(action); //$NON-NLS-1$
		auditoria.setUsuari(auditedUser);
		auditoria.setAutor(auditedUser);
		auditoria.setPasswordDomain(passwordDomain);
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"dd/MM/yyyy kk:mm:ss"); //$NON-NLS-1$
		auditoria.setData(dateFormat.format(GregorianCalendar.getInstance()
				.getTime()));
		auditoria.setObjecte(object); //$NON-NLS-1$
		auditoria.setMessage(question);

		AuditoriaEntity auditoriaEntity = getAuditoriaEntityDao()
				.auditoriaToEntity(auditoria);
		getAuditoriaEntityDao().create(auditoriaEntity);
	}

	@Override
	protected RememberPasswordChallenge handleRequestChallenge(String user)
			throws Exception {
		
		RememberPasswordChallenge challenge = new RememberPasswordChallenge();

		challenge.setMethod(guessRecoveryMethod (user));
		
		if (challenge.getMethod() == null)
			throw new InternalErrorException(
					Messages.getString("RememberPasswordUserServiceImpl.UserQuestionsError")); //$NON-NLS-1$

		int request;
		int right;
		if (challenge.getMethod().equals(RecoverMethodEnum.RECOVER_BY_QUESTIONS))
		{
			request = getRememberPasswordService()
					.getRememberPassConfiguration().getQuestions();
			right = getRememberPasswordService()
					.getRememberPassConfiguration().getRight();
			if (request <= 0 || right <= 0)
				throw new InternalErrorException (Messages.getString("RememberPasswordUserServiceImpl.disabledFeature")); //$NON-NLS-1$
		}
		else
		{
			request = right = 1;
		}
	
		// Check existing user
		if (getUsuariEntityDao().findByCodi(user) != null) {
			Calendar date = Calendar.getInstance();

			challenge.setChallengeDate(date);

			date.add(Calendar.MINUTE, 30);
			challenge.setExpirationDate(date);
			if (challenge.getMethod().equals(RecoverMethodEnum.RECOVER_BY_QUESTIONS))
				challenge.setQuestions(generateQuestions(user));
			else
				challenge.setQuestions(generateEmail(challenge, user));
			challenge.setUser(user);
			challenge.setChallengId(new Long(challenge.hashCode()));
			synchronized (challenges) {
				challenges.put(challenge.getChallengId(), challenge);
			}
		} else {
			throw new UnknownUserException(Messages.getString("RememberPasswordUserServiceImpl.UserNotFoundError")); //$NON-NLS-1$
		}

		RememberPasswordChallenge challenge2 = new RememberPasswordChallenge(challenge);
		for (Iterator<UserAnswer> it = challenge2.getQuestions().iterator(); it.hasNext();)
		{
			it.next().setAnswer(null);
		}
		challenge2.setEmailPin(null);
		audit (challenge2.getUser(), null, "SC_RPANSW", "R", null); //$NON-NLS-1$ //$NON-NLS-2$
		return challenge2;
	}

}
