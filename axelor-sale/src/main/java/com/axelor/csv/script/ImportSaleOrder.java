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
package com.axelor.csv.script;

import com.axelor.apps.base.AxelorException;
import com.axelor.apps.base.service.administration.SequenceService;
import com.axelor.apps.sale.db.SaleOrder;
import com.axelor.apps.sale.db.repo.SaleOrderManagementRepository;
import com.axelor.apps.sale.db.repo.SaleOrderRepository;
import com.axelor.apps.sale.service.saleorder.SaleOrderComputeService;
import com.axelor.apps.sale.service.saleorder.SaleOrderService;
import com.axelor.apps.sale.service.saleorder.status.SaleOrderFinalizeService;
import com.axelor.apps.sale.service.saleorderline.SaleOrderLineComputeService;
import com.google.inject.Inject;
import java.util.Map;

public class ImportSaleOrder {

  @Inject SaleOrderManagementRepository saleOrderRepo;

  protected SaleOrderService saleOrderService;
  protected SaleOrderComputeService saleOrderComputeService;
  protected SaleOrderFinalizeService saleOrderFinalizeService;
  protected SequenceService sequenceService;
  protected SaleOrderLineComputeService saleOrderLineComputeService;

  @Inject
  public ImportSaleOrder(
      SaleOrderService saleOrderService,
      SaleOrderComputeService saleOrderComputeService,
      SaleOrderFinalizeService saleOrderFinalizeService,
      SequenceService sequenceService,
      SaleOrderLineComputeService saleOrderLineComputeService) {
    this.saleOrderService = saleOrderService;
    this.saleOrderComputeService = saleOrderComputeService;
    this.saleOrderFinalizeService = saleOrderFinalizeService;
    this.sequenceService = sequenceService;
    this.saleOrderLineComputeService = saleOrderLineComputeService;
  }

  public Object importSaleOrder(Object bean, Map<String, Object> values) throws AxelorException {
    assert bean instanceof SaleOrder;

    SaleOrder saleOrder = (SaleOrder) bean;

    saleOrderService.computeAddressStr(saleOrder);

    saleOrderLineComputeService.computeLevels(saleOrder.getSaleOrderLineList(), null);
    saleOrder = saleOrderComputeService.computeSaleOrder(saleOrder);

    if (saleOrder.getStatusSelect() == 1) {
      saleOrder.setSaleOrderSeq(sequenceService.getDraftSequenceNumber(saleOrder));
      saleOrderRepo.computeFullName(saleOrder);
    } else {
      // Setting the status to draft or else we can't finalize it.
      saleOrder.setStatusSelect(SaleOrderRepository.STATUS_DRAFT_QUOTATION);
      saleOrderFinalizeService.finalizeQuotation(saleOrder);
    }

    return saleOrder;
  }
}
