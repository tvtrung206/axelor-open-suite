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
package com.axelor.apps.account.db.repo;

import com.axelor.apps.account.db.PaymentVoucher;
import com.axelor.apps.account.exception.AccountExceptionMessage;
import com.axelor.apps.account.service.payment.paymentvoucher.PaymentVoucherSequenceService;
import com.axelor.apps.base.AxelorException;
import com.axelor.apps.base.db.repo.TraceBackRepository;
import com.axelor.apps.base.service.app.AppBaseService;
import com.axelor.apps.base.service.exception.TraceBackService;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import javax.persistence.PersistenceException;

public class PaymentVoucherManagementRepository extends PaymentVoucherRepository {

  @Override
  public PaymentVoucher copy(PaymentVoucher entity, boolean deep) {

    PaymentVoucher copy = super.copy(entity, deep);

    copy.setStatusSelect(STATUS_DRAFT);
    copy.setRef(null);
    copy.setPaymentDate(Beans.get(AppBaseService.class).getTodayDate(copy.getCompany()));
    copy.clearPayVoucherDueElementList();
    copy.clearPayVoucherElementToPayList();
    copy.setGeneratedMove(null);
    copy.setBankCardTransactionNumber(null);
    copy.clearBatchSet();
    copy.setImportId(null);
    copy.setReceiptNo(null);
    copy.setRemainingAmount(null);
    copy.setRemainingAllocatedAmount(null);
    copy.setToSaveEmailOk(false);
    copy.setDefaultEmailOk(false);
    copy.setEmail(null);

    return copy;
  }

  @Override
  public PaymentVoucher save(PaymentVoucher paymentVoucher) {
    try {

      Beans.get(PaymentVoucherSequenceService.class).setReference(paymentVoucher);

      return super.save(paymentVoucher);
    } catch (Exception e) {
      TraceBackService.traceExceptionFromSaveMethod(e);
      throw new PersistenceException(e.getMessage(), e);
    }
  }

  @Override
  public void remove(PaymentVoucher entity) {
    if (Beans.get(MoveRepository.class).findByPaymentVoucher(entity).count() > 0) {
      try {
        throw new AxelorException(
            TraceBackRepository.CATEGORY_CONFIGURATION_ERROR,
            I18n.get(AccountExceptionMessage.PAYMENT_VOUCHER_REMOVE_NOT_OK));
      } catch (AxelorException e) {
        throw new PersistenceException(e.getMessage(), e);
      }
    }
    super.remove(entity);
  }
}
