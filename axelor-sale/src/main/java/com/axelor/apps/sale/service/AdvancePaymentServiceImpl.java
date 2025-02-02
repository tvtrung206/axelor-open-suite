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
package com.axelor.apps.sale.service;

import com.axelor.apps.sale.db.AdvancePayment;
import com.axelor.apps.sale.db.repo.AdvancePaymentRepository;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

public class AdvancePaymentServiceImpl implements AdvancePaymentService {

  @Inject protected AdvancePaymentRepository advancePaymentRepository;

  @Transactional
  public void cancelAdvancePayment(AdvancePayment advancePayment) {

    advancePayment.setStatusSelect(AdvancePaymentRepository.STATUS_CANCELED);
    advancePaymentRepository.save(advancePayment);

    // Relancer le calcul du montant d'acompte sur le devis.

  }
}
