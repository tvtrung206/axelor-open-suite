/*
 * Axelor Business Solutions
 *
 * Copyright (C) 2005-2025 Axelor (<http://axelor.com>).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.axelor.apps.account.service;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.repo.InvoiceRepository;
import com.axelor.apps.account.service.app.AppAccountService;
import com.axelor.apps.account.service.config.AccountConfigService;
import com.axelor.apps.base.AxelorException;
import com.axelor.apps.base.db.Company;
import com.google.inject.Inject;

public class PfpServiceImpl implements PfpService {

  protected AppAccountService appAccountService;
  protected AccountConfigService accountConfigService;

  @Inject
  public PfpServiceImpl(
      AppAccountService appAccountService, AccountConfigService accountConfigService) {
    this.appAccountService = appAccountService;
    this.accountConfigService = accountConfigService;
  }

  @Override
  public boolean isManagePassedForPayment(Company company) throws AxelorException {
    return company != null
        && this.isActivatePassedForPayment()
        && accountConfigService.getAccountConfig(company).getIsManagePassedForPayment();
  }

  @Override
  public boolean isManagePFPInRefund(Company company) throws AxelorException {
    return company != null
        && this.isActivatePassedForPayment()
        && accountConfigService.getAccountConfig(company).getIsManagePFPInRefund();
  }

  @Override
  public boolean isManageDaybookInPFP(Company company) throws AxelorException {
    return company != null
        && this.isActivatePassedForPayment()
        && accountConfigService.getAccountConfig(company).getIsManageDaybookInPFP();
  }

  protected boolean isActivatePassedForPayment() {
    return appAccountService.getAppAccount() != null
        && appAccountService.getAppAccount().getActivatePassedForPayment();
  }

  @Override
  public boolean getPfpCondition(Invoice invoice) throws AxelorException {
    return this.isManagePassedForPayment(invoice.getCompany())
        && this.getOperationTypePurchaseCondition(invoice);
  }

  @Override
  public boolean getOperationTypePurchaseCondition(Invoice invoice) throws AxelorException {
    return invoice.getOperationTypeSelect() == InvoiceRepository.OPERATION_TYPE_SUPPLIER_PURCHASE
        || (invoice.getOperationTypeSelect() == InvoiceRepository.OPERATION_TYPE_SUPPLIER_REFUND
            && this.isManagePFPInRefund(invoice.getCompany()));
  }
}
