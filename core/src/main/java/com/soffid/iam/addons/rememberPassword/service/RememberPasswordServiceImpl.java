/**
 * 
 */
package com.soffid.iam.addons.rememberPassword.service;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import org.apache.axis.InternalException;

import com.soffid.iam.addons.rememberPassword.common.DefaultQuestion;
import com.soffid.iam.addons.rememberPassword.common.RecoverMethodEnum;
import com.soffid.iam.addons.rememberPassword.common.RememberPassConfig;
import com.soffid.iam.addons.rememberPassword.common.RequireQuestion;
import com.soffid.iam.addons.rememberPassword.common.UserAnswer;
import com.soffid.iam.addons.rememberPassword.model.DefaultQuestionEntity;
import com.soffid.iam.addons.rememberPassword.model.DefaultQuestionEntityDao;
import com.soffid.iam.addons.rememberPassword.model.UserAnswerEntity;

import es.caib.seycon.ng.comu.Auditoria;
import es.caib.seycon.ng.comu.Configuracio;
import es.caib.seycon.ng.model.AuditoriaEntity;
import es.caib.seycon.ng.utils.Security;

/**
 * @author (C) Soffid 2014
 * 
 */
public class RememberPasswordServiceImpl extends RememberPasswordServiceBase {

	private static final String ALLOW_QUESTION_PROPERTY = "addon.retrieve.password.allow-question";
	private static final String ALLOW_EMAIL_PROPERTY = "addon.retrieve.password.allow-mail";
	private static final String PREFERRED_METHOD_PROPERTY = "addon.retrieve.password.preferred-method";
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

		DefaultQuestionEntity entity = getDefaultQuestionEntityDao()
				.defaultQuestionToEntity(question);
		getDefaultQuestionEntityDao().create(entity);
		question.setId(entity.getId());

		audit (null, question.getQuestion(), "SC_RPQUES", "C"); //$NON-NLS-1$ //$NON-NLS-2$
		return getDefaultQuestionEntityDao().toDefaultQuestion(entity);
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
		DefaultQuestionEntity entity = getDefaultQuestionEntityDao()
				.defaultQuestionToEntity(question);
		getDefaultQuestionEntityDao().update(entity);

		audit (null, question.getQuestion(), "SC_RPQUES", "U"); //$NON-NLS-1$ //$NON-NLS-2$
		return getDefaultQuestionEntityDao().toDefaultQuestion(entity);
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
	protected void handleUpdate(RememberPassConfig config) throws Exception {
		Configuracio toUpdate = null;

		checkConfigurationValues(config);

		toUpdate = getConfiguracioService().findParametreByCodiAndCodiXarxa(
				require, null);
		if (toUpdate == null) {
			getConfiguracioService()
					.create(new Configuracio(
							require,
							config.getRequired().toString(),
							null,
							Messages.getString("RememberPasswordServiceImpl.RequiredOption"), //$NON-NLS-1$
							null));
		}

		else {
			toUpdate.setValor(config.getRequired().toString());
			getConfiguracioService().update(toUpdate);
		}

		toUpdate = getConfiguracioService().findParametreByCodiAndCodiXarxa(
				fillin_number, null);
		if (toUpdate == null) {
			getConfiguracioService()
					.create(new Configuracio(
							fillin_number,
							config.getNumber().toString(),
							null,
							Messages.getString("RememberPasswordServiceImpl.QuestionsNumber"), null)); //$NON-NLS-1$
		}

		else {
			toUpdate.setValor(config.getNumber().toString());
			getConfiguracioService().update(toUpdate);
		}

		toUpdate = getConfiguracioService().findParametreByCodiAndCodiXarxa(
				query_number, null);
		if (toUpdate == null) {
			getConfiguracioService()
					.create(new Configuracio(
							query_number,
							config.getQuestions().toString(),
							null,
							Messages.getString("RememberPasswordServiceImpl.QuestionsNumber"), null)); //$NON-NLS-1$
		}

		else {
			toUpdate.setValor(config.getQuestions().toString());
			getConfiguracioService().update(toUpdate);
		}

		toUpdate = getConfiguracioService().findParametreByCodiAndCodiXarxa(
				right_number, null);
		if (toUpdate == null) {
			getConfiguracioService()
					.create(new Configuracio(
							right_number,
							config.getRight().toString(),
							null,
							Messages.getString("RememberPasswordServiceImpl.RightQuestionsNumber"), //$NON-NLS-1$
							null));
		}

		else {
			toUpdate.setValor(config.getRight().toString());
			getConfiguracioService().update(toUpdate);
		}
		
		toUpdate = getConfiguracioService().findParametreByCodiAndCodiXarxa(
				ALLOW_EMAIL_PROPERTY, null);
		if (toUpdate == null) {
			getConfiguracioService()
					.create(new Configuracio(
							ALLOW_EMAIL_PROPERTY,
							Boolean.toString(config.isAllowMailRecovery()),
							null,
							"Allow email recovery",
							null));
		}
		else 
		{
			toUpdate.setValor(Boolean.toString(config.isAllowMailRecovery()));
			getConfiguracioService().update(toUpdate);
		}

		toUpdate = getConfiguracioService().findParametreByCodiAndCodiXarxa(
				ALLOW_QUESTION_PROPERTY, null);
		if (toUpdate == null) {
			getConfiguracioService()
					.create(new Configuracio(
							ALLOW_QUESTION_PROPERTY,
							Boolean.toString(config.isAllowQuestionRecovery()),
							null,
							"Allow question recovery",
							null));
		}
		else 
		{
			toUpdate.setValor(Boolean.toString(config.isAllowQuestionRecovery()));
			getConfiguracioService().update(toUpdate);
		}

		toUpdate = getConfiguracioService().findParametreByCodiAndCodiXarxa(
				PREFERRED_METHOD_PROPERTY, null);
		if (toUpdate == null) {
			getConfiguracioService()
					.create(new Configuracio(
							PREFERRED_METHOD_PROPERTY,
							config.getPreferredMethod().getValue(),
							null,
							"Preferred password recovery method",
							null));
		}
		else 
		{
			toUpdate.setValor(config.getPreferredMethod().getValue());
			getConfiguracioService().update(toUpdate);
		}

		audit (null ,null, "SC_RPQUES", "G"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Check configuration parameters.
	 * 
	 * @param config
	 *            Configuration to check.
	 */
	private void checkConfigurationValues(RememberPassConfig config) {
		if (config.getRequired() != RequireQuestion.DISABLED) {
			if (config.getNumber() == 0)
				throw new InternalException(
						Messages.getString("RememberPasswordServiceImpl.ErrorQuestionsNumber")); //$NON-NLS-1$

			if (config.getRight() == 0)
				throw new InternalException(
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
	protected RememberPassConfig handleGetRememberPassConfiguration()
			throws Exception {
		RememberPassConfig configuration = new RememberPassConfig();
		String systemConfig = null;

		systemConfig = System.getProperty(require);
		configuration
				.setRequired((systemConfig != null) ? RequireQuestion
						.fromString(systemConfig)
						: RequireQuestion.DISABLED);

		systemConfig = System.getProperty(query_number);
		configuration.setQuestions((systemConfig != null) ? Integer
				.parseInt(systemConfig) : 0);

		systemConfig = System.getProperty(fillin_number);
		if (systemConfig == null)
			systemConfig = System.getProperty(query_number);
		configuration.setNumber((systemConfig != null) ? Integer
				.parseInt(systemConfig) : 0);

		systemConfig = System.getProperty(right_number);
		configuration.setRight((systemConfig != null) ? Integer
				.parseInt(systemConfig) : 0);
		
		systemConfig = System.getProperty(ALLOW_QUESTION_PROPERTY);
		configuration.setAllowQuestionRecovery("true".equals(systemConfig));

		systemConfig = System.getProperty(ALLOW_EMAIL_PROPERTY);
		configuration.setAllowMailRecovery("true".equals(systemConfig));

		systemConfig = System.getProperty(PREFERRED_METHOD_PROPERTY);
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
		RememberPassConfig config = getRememberPassConfiguration();
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
		Auditoria auditoria = new Auditoria();
		auditoria.setAccio(action); //$NON-NLS-1$
		auditoria.setUsuari(auditedUser);
		auditoria.setAutor(codiUsuari);
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

}
