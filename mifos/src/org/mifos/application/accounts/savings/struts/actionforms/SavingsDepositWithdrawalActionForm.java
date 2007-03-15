package org.mifos.application.accounts.savings.struts.actionforms;

import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.exceptions.InvalidDateException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.StringUtils;

public class SavingsDepositWithdrawalActionForm extends BaseActionForm {
	String trxnTypeId;

	String paymentTypeId;

	String trxnDate;

	String receiptDate;

	String receiptId;

	String customerId;

	String amount;

	public SavingsDepositWithdrawalActionForm() {
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getPaymentTypeId() {
		return paymentTypeId;
	}

	public void setPaymentTypeId(String paymentTypeId) {
		this.paymentTypeId = paymentTypeId;
	}

	public String getReceiptDate() {
		return receiptDate;
	}

	public void setReceiptDate(String receiptDate) {
		this.receiptDate = receiptDate;
	}

	public String getReceiptId() {
		return receiptId;
	}

	public void setReceiptId(String receiptId) {
		this.receiptId = receiptId;
	}

	public String getTrxnDate() {
		return trxnDate;
	}

	public void setTrxnDate(String trxnDate) {
		this.trxnDate = trxnDate;
	}

	public String getTrxnTypeId() {
		return trxnTypeId;
	}

	public void setTrxnTypeId(String trxnTypeId) {
		this.trxnTypeId = trxnTypeId;
	}

	public Money getAmountValue() {
		return getMoney(amount);
	}

	@Override
	public ActionErrors validate(ActionMapping mapping,
			HttpServletRequest request) {
		String method = request.getParameter("method");
		ActionErrors errors = null;
		UserContext userContext = (UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, request.getSession());
		ResourceBundle resources = ResourceBundle
				.getBundle(
						"org.mifos.application.accounts.savings.util.resources.SavingsUIResources",
						userContext.getPereferedLocale());

		if (method != null && method.equals(Methods.preview.toString())) {
			errors = new ActionErrors();

			if (StringUtils.isNullOrEmpty(getTrxnTypeId())) {
				errors.add(AccountConstants.ERROR_MANDATORY, new ActionMessage(
						AccountConstants.ERROR_MANDATORY, resources
								.getString("Savings.paymentType")));
			}

			if (getAmount() == null
					|| getAmountValue().getAmountDoubleValue() <= 0.0)
				errors.add(AccountConstants.ERROR_MANDATORY, new ActionMessage(
						AccountConstants.ERROR_MANDATORY, resources
								.getString("Savings.amount")));

			if (StringUtils.isNullOrEmpty(getPaymentTypeId())) {
				errors.add(AccountConstants.ERROR_MANDATORY, new ActionMessage(
						AccountConstants.ERROR_MANDATORY, resources
								.getString("Savings.modeOfPayment")));
			}

			ActionErrors dateError = validateDate(this.trxnDate, resources
					.getString("Savings.dateOfTrxn"), userContext);
			if (dateError != null && !dateError.isEmpty())
				errors.add(dateError);

			if (this.getReceiptDate() != null
					&& !this.getReceiptDate().equals("")) {
				dateError = validateDate(getReceiptDate(), resources
						.getString("Savings.receiptDate"), userContext);
				if (dateError != null && !dateError.isEmpty())
					errors.add(dateError);
			}
			if (StringUtils.isNullOrEmpty(getCustomerId())) {
				errors.add(AccountConstants.ERROR_MANDATORY, new ActionMessage(
						AccountConstants.ERROR_MANDATORY, resources
								.getString("Savings.ClientName")));
			}
		}

		if (null != errors && !errors.isEmpty()) {
			request.setAttribute(Globals.ERROR_KEY, errors);
			request.setAttribute("methodCalled", method);
		}

		return errors;
	}

	private ActionErrors validateDate(String date, String fieldName,
			UserContext userContext) {
		ActionErrors errors = null;
		java.sql.Date sqlDate = null;
		if (date != null && !date.equals("")) {
			try {
				sqlDate = DateHelper.getDateAsSentFromBrowser(date);
				if (DateHelper.whichDirection(sqlDate) > 0) {
					errors = new ActionErrors();
					errors.add(AccountConstants.ERROR_FUTUREDATE,
							new ActionMessage(
									AccountConstants.ERROR_FUTUREDATE,
									fieldName));
				}
			}
			catch (InvalidDateException e) {
				errors = new ActionErrors();
				errors.add(AccountConstants.ERROR_INVALIDDATE,
						new ActionMessage(AccountConstants.ERROR_INVALIDDATE,
								fieldName));
			}
		}
		else {
			errors = new ActionErrors();
			errors.add(AccountConstants.ERROR_MANDATORY, new ActionMessage(
					AccountConstants.ERROR_MANDATORY, fieldName));
		}
		return errors;
	}
}
