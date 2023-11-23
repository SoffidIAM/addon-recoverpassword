/**
 * 
 */
package com.soffid.iam.addons.passrecover.service;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import com.soffid.iam.addons.passrecover.common.DefaultQuestion;
import com.soffid.iam.addons.passrecover.common.RecoverMethodEnum;
import com.soffid.iam.addons.passrecover.common.RecoverPassConfig;
import com.soffid.iam.addons.passrecover.common.RequireQuestion;
import com.soffid.iam.addons.passrecover.common.UserAnswer;
import com.soffid.iam.addons.passrecover.model.DefaultQuestionEntity;
import com.soffid.iam.addons.passrecover.model.DefaultQuestionEntityDao;
import com.soffid.iam.addons.passrecover.model.UserAnswerEntity;
import com.soffid.iam.api.Audit;
import com.soffid.iam.api.Configuration;
import com.soffid.iam.model.AuditEntity;
import com.soffid.iam.utils.ConfigurationCache;
import com.soffid.iam.utils.Security;

import es.caib.seycon.ng.exception.InternalErrorException;

/**
 * @author (C) Soffid 2014
 * 
 */
public class RecoverPasswordServiceImpl extends RecoverPasswordServiceBase {

	private static final String ALLOW_QUESTION_PROPERTY = "addon.retrieve.password.allow-question";
	private static final String ALLOW_EMAIL_PROPERTY = "addon.retrieve.password.allow-mail";
	private static final String ALLOW_PASSWORD_REUSE = "addon.retrieve.password.allow-reuse";
	private static final String PREFERRED_METHOD_PROPERTY = "addon.retrieve.password.preferred-method";
	private static final String ALLOW_SMS_PROPERTY = "addon.retrieve.password.allow-sms";
	private static final String ALLOW_OTP_PROPERTY = "addon.retrieve.password.allow-otp";
	private static final String EMAIL_SUBJECT_PROPERTY = "addon.retrieve.password.email-subject";
	private static final String EMAIL_BODY_PROPERTY = "addon.retrieve.password.email-body";
	private static final String SMS_ATTRIBUTE = "addon.retrieve.password.sms-attribute";
	private static final String SMS_URL = "addon.retrieve.password.sms-url";
	private static final String SMS_METHOD = "addon.retrieve.password.sms-methdo";
	private static final String SMS_BODY = "addon.retrieve.password.sms-boty";
	private static final String SMS_HEADERS = "addon.retrieve.password.sms-headers";
	private static final String SMS_CHECK = "addon.retrieve.password.sms-check";
	static String require = "addon.retrieve-password.require"; //$NON-NLS-1$
	static String fillin_number = "addon.retrieve-password.fillin_number"; //$NON-NLS-1$
	static String query_number = "addon.retrieve-password.query_number"; //$NON-NLS-1$
	static String right_number = "addon.retrieve-password.right_number"; //$NON-NLS-1$

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.soffid.iam.addons.rememberPassword.service.RememberPasswordServiceBase
	 * #handleFindAllDefaultQuestions()
	 */
	@Override
	protected Collection<DefaultQuestion> handleFindAllDefaultQuestions()
			throws Exception {
		DefaultQuestionEntityDao dao = getDefaultQuestionEntityDao();
		return dao.toDefaultQuestionList(dao.loadAll());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.soffid.iam.addons.rememberPassword.service.RememberPasswordServiceBase
	 * #
	 * handleCreate(com.soffid.iam.addons.rememberPassword.common.DefaultQuestion
	 * )
	 */
	@Override
	protected DefaultQuestion handleCreate(DefaultQuestion question)
			throws Exception {

		if (question.getQuestion() == null || question.getQuestion().trim().isEmpty()) {
			// Nothing to do
			return question;
		} else {
			DefaultQuestionEntity entity = getDefaultQuestionEntityDao()
					.defaultQuestionToEntity(question);
			getDefaultQuestionEntityDao().create(entity);
			question.setId(entity.getId());

			audit (null, question.getQuestion(), "SC_RPQUES", "C"); //$NON-NLS-1$ //$NON-NLS-2$
			return getDefaultQuestionEntityDao().toDefaultQuestion(entity);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.soffid.iam.addons.rememberPassword.service.RememberPasswordServiceBase
	 * #
	 * handleDelete(com.soffid.iam.addons.rememberPassword.common.DefaultQuestion
	 * )
	 */
	@Override
	protected void handleDelete(DefaultQuestion question) throws Exception {

		audit (null, question.getQuestion(), "SC_RPQUES", "R"); //$NON-NLS-1$ //$NON-NLS-2$
		getDefaultQuestionEntityDao()
				.remove(getDefaultQuestionEntityDao().defaultQuestionToEntity(
						question));

		new SignalGenerator().generateRecoveryChangeEvent(entity.getUser().getUserName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.soffid.iam.addons.rememberPassword.service.RememberPasswordServiceBase
	 * #
	 * handleUpdate(com.soffid.iam.addons.rememberPassword.common.DefaultQuestion
	 * )
	 */
	@Override
	protected DefaultQuestion handleUpdate(DefaultQuestion question)
			throws Exception {
		if (question.getQuestion() == null || question.getQuestion().trim().isEmpty()) {
			handleDelete(question);
			question.setId(null);	
			return question;
		} else {
			DefaultQuestionEntity entity = getDefaultQuestionEntityDao()
					.defaultQuestionToEntity(question);
			if (entity.getId() == null) {
				getDefaultQuestionEntityDao().create(entity);
		
				audit (null, question.getQuestion(), "SC_RPQUES", "C"); //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				getDefaultQuestionEntityDao().update(entity);
				
				audit (null, question.getQuestion(), "SC_RPQUES", "U"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			return getDefaultQuestionEntityDao().toDefaultQuestion(entity);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.soffid.iam.addons.rememberPassword.service.RememberPasswordServiceBase
	 * #
	 * handleUpdate(com.soffid.iam.addons.rememberPassword.common.RememberPassConfig
	 * )
	 */
	@Override
	protected void handleUpdate(RecoverPassConfig config) throws Exception {
		Configuration toUpdate = null;

		checkConfigurationValues(config);

		toUpdate = getConfigurationService().findParameterByNameAndNetworkName(
				require, null);
		if (toUpdate == null) {
			getConfigurationService()
					.create(new Configuration(
							require,
							config.getRequired().toString(),
							null,
							Messages.getString("RememberPasswordServiceImpl.RequiredOption"), //$NON-NLS-1$
							null));
		}

		else {
			toUpdate.setValue(config.getRequired().toString());
			getConfigurationService().update(toUpdate);
		}

		toUpdate = getConfigurationService().findParameterByNameAndNetworkName(
				fillin_number, null);
		if (toUpdate == null) {
			getConfigurationService()
					.create(new Configuration(
							fillin_number,
							config.getNumber().toString(),
							null,
							Messages.getString("RememberPasswordServiceImpl.QuestionsNumber"), null)); //$NON-NLS-1$
		}

		else {
			toUpdate.setValue(config.getNumber().toString());
			getConfigurationService().update(toUpdate);
		}

		toUpdate = getConfigurationService().findParameterByNameAndNetworkName(
				query_number, null);
		if (toUpdate == null) {
			getConfigurationService()
					.create(new Configuration(
							query_number,
							config.getQuestions().toString(),
							null,
							Messages.getString("RememberPasswordServiceImpl.QuestionsNumber"), null)); //$NON-NLS-1$
		}

		else {
			toUpdate.setValue(config.getQuestions().toString());
			getConfigurationService().update(toUpdate);
		}

		toUpdate = getConfigurationService().findParameterByNameAndNetworkName(
				right_number, null);
		if (toUpdate == null) {
			getConfigurationService()
					.create(new Configuration(
							right_number,
							config.getRight().toString(),
							null,
							Messages.getString("RememberPasswordServiceImpl.RightQuestionsNumber"), //$NON-NLS-1$
							null));
		}

		else {
			toUpdate.setValue(config.getRight().toString());
			getConfigurationService().update(toUpdate);
		}
		
		toUpdate = getConfigurationService().findParameterByNameAndNetworkName(
				ALLOW_EMAIL_PROPERTY, null);
		if (toUpdate == null) {
			getConfigurationService()
					.create(new Configuration(
							ALLOW_EMAIL_PROPERTY,
							Boolean.toString(config.isAllowMailRecovery()),
							null,
							"Allow email recovery",
							null));
		}
		else 
		{
			toUpdate.setValue(Boolean.toString(config.isAllowMailRecovery()));
			getConfigurationService().update(toUpdate);
		}

		toUpdate = getConfigurationService().findParameterByNameAndNetworkName(
				ALLOW_PASSWORD_REUSE, null);
		if (toUpdate == null) {
			getConfigurationService()
					.create(new Configuration(
							ALLOW_PASSWORD_REUSE,
							Boolean.toString(config.isAllowPasswordReuse()),
							null,
							"Allow reuse password",
							null));
		}
		else 
		{
			toUpdate.setValue(Boolean.toString(config.isAllowPasswordReuse()));
			getConfigurationService().update(toUpdate);
		}

		toUpdate = getConfigurationService().findParameterByNameAndNetworkName(
				ALLOW_QUESTION_PROPERTY, null);
		if (toUpdate == null) {
			getConfigurationService()
					.create(new Configuration(
							ALLOW_QUESTION_PROPERTY,
							Boolean.toString(config.isAllowQuestionRecovery()),
							null,
							"Allow question recovery",
							null));
		}
		else 
		{
			toUpdate.setValue(Boolean.toString(config.isAllowQuestionRecovery()));
			getConfigurationService().update(toUpdate);
		}

		toUpdate = getConfigurationService().findParameterByNameAndNetworkName(
				PREFERRED_METHOD_PROPERTY, null);
		if (toUpdate == null) {
			getConfigurationService()
					.create(new Configuration(
							PREFERRED_METHOD_PROPERTY,
							config.getPreferredMethod().getValue(),
							null,
							"Preferred password recovery method",
							null));
		}
		else 
		{
			toUpdate.setValue(config.getPreferredMethod().getValue());
			getConfigurationService().update(toUpdate);
		}

		toUpdate = getConfigurationService().findParameterByNameAndNetworkName(
				EMAIL_SUBJECT_PROPERTY, null);
		if (toUpdate == null) {
			getConfigurationService()
					.create(new Configuration(
							EMAIL_SUBJECT_PROPERTY,
							config.getEmailSubject(),
							null,
							"Email recovery subject",
							null));
		}
		else 
		{
			toUpdate.setValue(config.getEmailSubject());
			getConfigurationService().update(toUpdate);
		}
		
		if (config.getEmailBody() == null)
			getConfigurationService().deleteBlob(EMAIL_BODY_PROPERTY);
		else
			getConfigurationService().updateBlob(EMAIL_BODY_PROPERTY, config.getEmailBody().getBytes(StandardCharsets.UTF_8));

		updateConfig ( ALLOW_OTP_PROPERTY, config.isAllowOtpRecovery(), "Allow OTP Recovery");
		updateConfig ( ALLOW_SMS_PROPERTY, config.isAllowSmsRecovery(), "Allow SMS Recovery");
		updateConfig ( SMS_BODY, config.getSmsBody(), "SMS Service body");
		updateConfig ( SMS_HEADERS, config.getSmsHeaders(), "SMS Service headers");
		updateConfig ( SMS_CHECK, config.getSmsResponseToCheck(), "SMS Service response check");
		updateConfig ( SMS_URL, config.getSmsUrl(), "SMS Service URL");
		updateConfig ( SMS_METHOD, config.getSmsMethod(), "SMS Service method");
		updateConfig ( SMS_ATTRIBUTE, config.getSmsAttribute(), "SMS Service phone attribute");
		
		audit (null ,null, "SC_RPQUES", "G"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private void updateConfig(String property, Object value, String desc) throws InternalErrorException {
		Configuration toUpdate = getConfigurationService().findParameterByNameAndNetworkName(
				property, null);
		if (toUpdate == null && value != null && !value.toString().trim().isEmpty()) {
			getConfigurationService()
					.create(new Configuration(
							property,
							value.toString(),
							null,
							"Preferred password recovery method",
							null));
		}
		else if (value == null || value.toString().trim().isEmpty()) {
			if (toUpdate != null)
				getConfigurationService().delete(toUpdate);
		}
		else 
		{
			toUpdate.setValue(value.toString());
			getConfigurationService().update(toUpdate);
		}
	}

	/**
	 * Check configuration parameters.
	 * 
	 * @param config
	 *            Configuration to check.
	 * @throws InternalErrorException 
	 */
	private void checkConfigurationValues(RecoverPassConfig config) throws InternalErrorException {
		if (config.getRequired() != RequireQuestion.DISABLED) {
			if (config.getNumber() == 0)
				throw new InternalErrorException(
						Messages.getString("RememberPasswordServiceImpl.ErrorQuestionsNumber")); //$NON-NLS-1$

			if (config.getRight() == 0)
				throw new InternalErrorException(
						Messages.getString("RememberPasswordServiceImpl.ErrorRitghtQuestions")); //$NON-NLS-1$
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.soffid.iam.addons.rememberPassword.service.RememberPasswordServiceBase
	 * #handleGetRememberPassConfiguration()
	 */
	@Override
	protected RecoverPassConfig handleGetRecoverPassConfiguration()
			throws Exception {
		RecoverPassConfig configuration = new RecoverPassConfig();
		String systemConfig = null;

		systemConfig = ConfigurationCache.getProperty(require);
		configuration
				.setRequired((systemConfig != null) ? RequireQuestion
						.fromString(systemConfig)
						: RequireQuestion.DISABLED);

		systemConfig = ConfigurationCache.getProperty(query_number);
		configuration.setQuestions((systemConfig != null) ? Integer
				.parseInt(systemConfig) : 0);

		systemConfig = ConfigurationCache.getProperty(fillin_number);
		if (systemConfig == null)
			systemConfig = ConfigurationCache.getProperty(query_number);
		configuration.setNumber((systemConfig != null) ? Integer
				.parseInt(systemConfig) : 0);

		systemConfig = ConfigurationCache.getProperty(right_number);
		configuration.setRight((systemConfig != null) ? Integer
				.parseInt(systemConfig) : 0);
		
		systemConfig = ConfigurationCache.getProperty(ALLOW_QUESTION_PROPERTY);
		configuration.setAllowQuestionRecovery("true".equals(systemConfig));

		systemConfig = ConfigurationCache.getProperty(ALLOW_EMAIL_PROPERTY);
		configuration.setAllowMailRecovery("true".equals(systemConfig));

		systemConfig = ConfigurationCache.getProperty(ALLOW_PASSWORD_REUSE);
		configuration.setAllowPasswordReuse("true".equals(systemConfig));

		systemConfig = ConfigurationCache.getProperty(ALLOW_SMS_PROPERTY);
		configuration.setAllowSmsRecovery("true".equals(systemConfig));

		systemConfig = ConfigurationCache.getProperty(ALLOW_OTP_PROPERTY);
		configuration.setAllowOtpRecovery("true".equals(systemConfig));

		systemConfig = ConfigurationCache.getProperty(SMS_URL);
		configuration.setSmsUrl( systemConfig  == null || systemConfig.trim().isEmpty() ? 
			"https://www.ovh.com/cgi-bin/sms/http2sms.cgi?account=...&password=...&login=...&from=...&"
			+ "to=${PHONE}&"
			+ "message=This is your PIN for password recovery: ${PIN}&"
			+ "noStop&contentType=application/json&class=0": 
			systemConfig);

		systemConfig = ConfigurationCache.getProperty(SMS_METHOD);
		configuration.setSmsMethod( systemConfig  == null || systemConfig.trim().isEmpty() ? "GET": systemConfig);

		systemConfig = ConfigurationCache.getProperty(SMS_BODY);
		configuration.setSmsBody( systemConfig  == null || systemConfig.trim().isEmpty() ? "": systemConfig);

		systemConfig = ConfigurationCache.getProperty(SMS_HEADERS);
		configuration.setSmsHeaders( systemConfig  == null || systemConfig.trim().isEmpty() ? "": systemConfig);

		systemConfig = ConfigurationCache.getProperty(SMS_CHECK);
		configuration.setSmsResponseToCheck( systemConfig  == null || systemConfig.trim().isEmpty() ? "": systemConfig);

		systemConfig = ConfigurationCache.getProperty(SMS_ATTRIBUTE);
		configuration.setSmsAttribute( systemConfig  == null || systemConfig.trim().isEmpty() ? "PHONE": systemConfig);

		systemConfig = ConfigurationCache.getProperty(PREFERRED_METHOD_PROPERTY);
		if (systemConfig == null)
			configuration.setPreferredMethod(RecoverMethodEnum.RECOVER_BY_QUESTIONS);
		else
		{
			try 
			{
				configuration.setPreferredMethod(RecoverMethodEnum.fromString(systemConfig));
			} catch (Exception e)
			{
				configuration.setPreferredMethod(RecoverMethodEnum.RECOVER_BY_QUESTIONS);
			}
		}
		
		systemConfig = ConfigurationCache.getProperty(EMAIL_SUBJECT_PROPERTY);
		configuration.setEmailSubject(systemConfig);
		
		byte[] data = getConfigurationService().getBlob(EMAIL_BODY_PROPERTY);
		configuration.setEmailBody(data == null ? null: new String(data, StandardCharsets.UTF_8));
		
		return configuration;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.soffid.iam.addons.rememberPassword.service.RememberPasswordServiceBase
	 * #handleFindUserAnswersByUserName(java.lang.String)
	 */
	@Override
	protected Collection<UserAnswer> handleFindUserAnswersByUserName(
			String userName) throws Exception {
		Collection<UserAnswerEntity> list = getUserAnswerEntityDao()
				.findByUser(userName);
		List<DefaultQuestionEntity> questions = getDefaultQuestionEntityDao()
				.loadAll();
		List<UserAnswer> answers = new LinkedList<UserAnswer>();
		for (DefaultQuestionEntity q : questions) {
			boolean found = false;
			for (UserAnswerEntity a : list) {
				if (a.getQuestion().equals(q.getQuestion())) {
					answers.add(getUserAnswerEntityDao().toUserAnswer(a));
					list.remove(a);
					found = true;
					break;
				}
			}
			if (!found) {
				UserAnswer answer = new UserAnswer();
				answer.setQuestion(q.getQuestion());
				answer.setUser(userName);
				UserAnswerEntity answerEntity = getUserAnswerEntityDao()
						.userAnswerToEntity(answer);
				getUserAnswerEntityDao().create(answerEntity);
				answers.add(getUserAnswerEntityDao().toUserAnswer(answerEntity));
			}
		}

		for (UserAnswerEntity a : list) {
			answers.add(getUserAnswerEntityDao().toUserAnswer(a));
		}

		return answers;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.soffid.iam.addons.rememberPassword.service.RememberPasswordServiceBase
	 * #handleUpdate(com.soffid.iam.addons.rememberPassword.common.UserAnswer)
	 */
	@Override
	protected UserAnswer handleUpdate(UserAnswer answer) throws Exception {
		UserAnswerEntity entity = getUserAnswerEntityDao().userAnswerToEntity(
				answer);
		getUserAnswerEntityDao().update(entity);

		audit (Security.getCurrentUser(), answer.getQuestion(), "SC_RPANSW", "U"); //$NON-NLS-1$ //$NON-NLS-2$

		new SignalGenerator().generateRecoveryChangeEvent(entity.getUser().getUserName());

		return getUserAnswerEntityDao().toUserAnswer(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.soffid.iam.addons.rememberPassword.service.RememberPasswordServiceBase
	 * #handleCreate(com.soffid.iam.addons.rememberPassword.common.UserAnswer)
	 */
	@Override
	protected UserAnswer handleCreate(UserAnswer answer) throws Exception {
		UserAnswerEntity entity = getUserAnswerEntityDao().userAnswerToEntity(
				answer);
		getUserAnswerEntityDao().create(entity);
		answer.setId(entity.getId());

		audit (Security.getCurrentUser(), answer.getQuestion(), "SC_RPANSW", "C"); //$NON-NLS-1$ //$NON-NLS-2$

		new SignalGenerator().generateRecoveryChangeEvent(entity.getUser().getUserName());
		
		return getUserAnswerEntityDao().toUserAnswer(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.soffid.iam.addons.rememberPassword.service.RememberPasswordServiceBase
	 * #handleRemove(com.soffid.iam.addons.rememberPassword.common.UserAnswer)
	 */
	@Override
	protected void handleRemove(UserAnswer answer) throws Exception {
		UserAnswerEntity entity = getUserAnswerEntityDao().userAnswerToEntity(
				answer);

		audit (Security.getCurrentUser(), answer.getQuestion(), "SC_RPANSW", "R"); //$NON-NLS-1$ //$NON-NLS-2$
		getUserAnswerEntityDao().remove(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.soffid.iam.addons.rememberPassword.service.RememberPasswordServiceBase
	 * #handleCheckUserConfiguration(java.lang.String)
	 */
	@Override
	protected boolean handleCheckUserConfiguration(String user)
			throws Exception {
		boolean userOK = true;
		int actualAnswers = 0;
		RecoverPassConfig config = getRecoverPassConfiguration();
		RequireQuestion required = config.getRequired();
		int answers = config.getNumber();

		List<UserAnswerEntity> userAnswers = getUserAnswerEntityDao()
				.findByUser(user);

		for (UserAnswerEntity userAnswerEntity : userAnswers) {
			if ((userAnswerEntity.getAnswer() != null) &&
					(! userAnswerEntity.getAnswer().isEmpty()))
				actualAnswers++;
		}

		if ((required == RequireQuestion.REQUIRED)
				&& actualAnswers < answers) {
			userOK = false;
		}

		return userOK;
	}

	private void audit(String auditedUser, String question, String object, String action) {
		String codiUsuari = Security.getCurrentAccount();
		Audit auditoria = new Audit();
		auditoria.setAction(action); //$NON-NLS-1$
		auditoria.setUser(auditedUser);
		auditoria.setAuthor(codiUsuari);
		auditoria.setCalendar(Calendar.getInstance());
		auditoria.setObject(object); //$NON-NLS-1$
		auditoria.setMessage(question);

		AuditEntity auditoriaEntity = getAuditEntityDao()
				.auditToEntity(auditoria);
		getAuditEntityDao().create(auditoriaEntity);
	}

}
