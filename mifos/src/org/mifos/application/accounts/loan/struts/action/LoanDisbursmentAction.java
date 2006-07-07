/**
 * 
 */
package org.mifos.application.accounts.loan.struts.action;

import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.service.AccountBusinessService;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.service.LoanBusinessService;
import org.mifos.application.accounts.loan.struts.actionforms.LoanDisbursmentActionForm;
import org.mifos.application.accounts.struts.actionforms.AccountApplyPaymentActionForm;
import org.mifos.application.master.business.service.MasterDataService;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.util.helpers.TrxnTypes;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;

public class LoanDisbursmentAction extends BaseAction {
	
	private LoanBusinessService loanBusinessService =null;
	private MasterDataService masterDataService=null;
	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		UserContext uc = (UserContext)SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY,request.getSession());
		LoanDisbursmentActionForm loanDisbursmentActionForm = (LoanDisbursmentActionForm)form;
		loanDisbursmentActionForm.clear();
		Date currentDate=new Date(System.currentTimeMillis());
		loanDisbursmentActionForm.setTransactionDate(DateHelper.getCurrentDate(uc.getPereferedLocale()));
		LoanBO  loan =  ((LoanBusinessService)getService()).getAccount(Integer.valueOf(loanDisbursmentActionForm.getAccountId()));
		loan.setUserContext(uc);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, loan, request.getSession());
		SessionUtils.setAttribute(MasterConstants.PAYMENT_TYPE,	getMasterDataService().getSupportedPaymentModes(uc.getLocaleId(),TrxnTypes.loan_repayment.getValue()),request.getSession());
		loanDisbursmentActionForm.setAmount(loan.getAmountTobePaidAtdisburtail(currentDate));
		loanDisbursmentActionForm.setLoanAmount(loan.getLoanAmount());
		return mapping.findForward(Constants.LOAD_SUCCESS);
	}

	public ActionForward preview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		
		return mapping.findForward(Constants.PREVIEW_SUCCESS);
	}

	public ActionForward previous(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(Constants.PREVIOUS_SUCCESS);
	}

	public ActionForward update(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		LoanBO savedloan = (LoanBO)SessionUtils.getAttribute(Constants.BUSINESS_KEY,request.getSession());
		LoanDisbursmentActionForm actionForm =(LoanDisbursmentActionForm)form;
		LoanBO loan = ((LoanBusinessService)getService()).getAccount(Integer.valueOf(actionForm.getAccountId()));
		loan.setVersionNo(savedloan.getVersionNo());
		UserContext uc = (UserContext)SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY,request.getSession());
		Date trxnDate = getDateFromString(actionForm.getTransactionDate(),uc.getPereferedLocale());
		Date receiptDate = getDateFromString(actionForm.getReceiptDate(),uc.getPereferedLocale());
		if(!loan.isTrxnDateValid(trxnDate)) throw new AccountException("errors.invalidTxndate");
		loan.disburseLoan(actionForm.getReceiptId(),trxnDate,Short.valueOf(actionForm.getPaymentTypeId()),uc.getId(),receiptDate,Short.valueOf(actionForm.getPaymentModeOfPayment()));
		return mapping.findForward(Constants.UPDATE_SUCCESS);
	}
	private Date getDateFromString(String strDate, Locale locale){
		Date date = null;
		if(strDate!=null && strDate!="")
			date = new Date(DateHelper.getLocaleDate(locale,strDate).getTime());
		return date;
	}
	private LoanBusinessService getLoanBusinessService()throws ServiceException{
		if(loanBusinessService==null)
			loanBusinessService =(LoanBusinessService)ServiceFactory.getInstance().getBusinessService(BusinessServiceName.Loan);
		return loanBusinessService;
	}
	
	private MasterDataService getMasterDataService()throws ServiceException{
		if(masterDataService==null)
			masterDataService =(MasterDataService)ServiceFactory.getInstance().getBusinessService(BusinessServiceName.MasterDataService);
		return masterDataService;
	}
	
	public ActionForward validate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String method = (String) request.getAttribute("methodCalled");
		String forward = null;
		if (method != null) {
			forward=method+"_failure";
		}
		return mapping.findForward(forward);
	}

	@Override
	protected BusinessService getService() throws ServiceException {
		return getLoanBusinessService();
	}

	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return true;
	}

	@Override
	protected boolean isNewBizRequired(HttpServletRequest request) throws ServiceException {
		return false;
	}
	
	
	
	
}
