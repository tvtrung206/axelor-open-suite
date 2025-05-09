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
package com.axelor.apps.account.service.move;

import com.axelor.apps.account.db.Move;
import com.axelor.apps.account.db.repo.InvoiceRepository;
import com.axelor.apps.account.db.repo.MoveRepository;
import com.axelor.apps.account.service.invoice.InvoiceTermPfpValidateService;
import com.axelor.auth.AuthUtils;
import com.axelor.auth.db.User;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

public class MovePfpValidateServiceImpl implements MovePfpValidateService {

  protected InvoiceTermPfpValidateService invoiceTermPfpValidateService;
  protected MoveToolService moveToolService;
  protected MoveRepository moveRepository;

  @Inject
  public MovePfpValidateServiceImpl(
      InvoiceTermPfpValidateService invoiceTermPfpValidateService,
      MoveToolService moveToolService,
      MoveRepository moveRepository) {
    this.invoiceTermPfpValidateService = invoiceTermPfpValidateService;
    this.moveToolService = moveToolService;
    this.moveRepository = moveRepository;
  }

  @Transactional
  @Override
  public void validatePfp(Long moveId) {
    Move move = moveRepository.find(moveId);
    User pfpValidatorUser =
        move.getPfpValidatorUser() != null ? move.getPfpValidatorUser() : AuthUtils.getUser();

    moveToolService
        ._getInvoiceTermList(move)
        .forEach(
            invoiceTerm ->
                invoiceTermPfpValidateService.validatePfp(invoiceTerm, pfpValidatorUser));

    move.setPfpValidatorUser(pfpValidatorUser);
    move.setPfpValidateStatusSelect(InvoiceRepository.PFP_STATUS_VALIDATED);
  }
}
