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
package com.axelor.apps.account.service.fixedasset;

import com.axelor.apps.account.db.FixedAsset;
import com.axelor.apps.account.db.FixedAssetLine;
import com.axelor.apps.account.db.repo.FixedAssetLineRepository;
import com.axelor.apps.account.db.repo.FixedAssetRepository;
import com.axelor.apps.base.AxelorException;
import com.axelor.apps.base.service.app.AppBaseService;
import com.google.inject.Inject;
import com.google.inject.servlet.RequestScoped;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RequestScoped
public class FixedAssetLineEconomicComputationServiceImpl
    extends AbstractFixedAssetLineComputationServiceImpl {

  protected FixedAssetDateService fixedAssetDateService;

  @Inject
  public FixedAssetLineEconomicComputationServiceImpl(
      FixedAssetDateService fixedAssetDateService,
      FixedAssetFailOverControlService fixedAssetFailOverControlService,
      AppBaseService appBaseService,
      FixedAssetLineToolService fixedAssetLineToolService) {
    super(fixedAssetFailOverControlService, appBaseService, fixedAssetLineToolService);
    this.fixedAssetDateService = fixedAssetDateService;
  }

  @Override
  protected LocalDate computeStartDepreciationDate(FixedAsset fixedAsset) {
    if (fixedAssetFailOverControlService.isFailOver(fixedAsset)
        && fixedAsset.getImportDepreciationDate().isAfter(fixedAsset.getFirstDepreciationDate())) {
      return fixedAsset.getImportDepreciationDate();
    }
    return fixedAsset.getFirstDepreciationDate();
  }

  @Override
  protected BigDecimal computeInitialDepreciationBase(FixedAsset fixedAsset) {
    if (fixedAssetFailOverControlService.isFailOver(fixedAsset)
        && getComputationMethodSelect(fixedAsset)
            .equals(FixedAssetRepository.COMPUTATION_METHOD_DEGRESSIVE)) {
      return fixedAssetLineToolService.getCompanyScaledValue(
          fixedAsset.getGrossValue(),
          getAlreadyDepreciatedAmount(fixedAsset),
          fixedAsset,
          BigDecimal::subtract);
    }
    if (!fixedAsset.getIsEqualToFiscalDepreciation()) {
      return fixedAssetLineToolService.getCompanyScaledValue(
          fixedAsset.getGrossValue(),
          fixedAsset.getResidualValue(),
          fixedAsset,
          BigDecimal::subtract);
    }
    return fixedAssetLineToolService.getCompanyScaledValue(fixedAsset.getGrossValue(), fixedAsset);
  }

  @Override
  protected LocalDate computeProrataTemporisFirstDepreciationDate(FixedAsset fixedAsset) {

    return fixedAsset.getFirstDepreciationDate();
  }

  @Override
  protected List<FixedAssetLine> getFixedAssetLineList(FixedAsset fixedAsset) {
    return fixedAsset.getFixedAssetLineList();
  }

  @Override
  protected BigDecimal getNumberOfDepreciation(FixedAsset fixedAsset) {
    return BigDecimal.valueOf(fixedAsset.getNumberOfDepreciation());
  }

  @Override
  protected String getComputationMethodSelect(FixedAsset fixedAsset) {
    return fixedAsset.getComputationMethodSelect();
  }

  @Override
  protected BigDecimal getDegressiveCoef(FixedAsset fixedAsset) {
    return fixedAsset.getDegressiveCoef();
  }

  @Override
  protected Integer getPeriodicityInMonth(FixedAsset fixedAsset) {
    return fixedAsset.getPeriodicityInMonth();
  }

  @Override
  protected Integer getTypeSelect() {

    return FixedAssetLineRepository.TYPE_SELECT_ECONOMIC;
  }

  @Override
  protected Boolean isProrataTemporis(FixedAsset fixedAsset) {
    if (fixedAssetFailOverControlService.isFailOver(fixedAsset)
        && fixedAsset.getImportNbrOfPastDepreciations() > 0) {
      // This case means that prorata temporis was already computed in another software.
      return false;
    }
    return fixedAsset.getFixedAssetCategory().getIsProrataTemporis();
  }

  @Override
  protected BigDecimal computeInitialDegressiveDepreciation(
      FixedAsset fixedAsset, BigDecimal baseValue) throws AxelorException {
    if (fixedAssetFailOverControlService.isFailOver(fixedAsset) && !isProrataTemporis(fixedAsset)) {
      FixedAssetLine dummyPreviousLine = new FixedAssetLine();
      dummyPreviousLine.setAccountingValue(baseValue);
      return super.computeOnGoingDegressiveDepreciation(fixedAsset, dummyPreviousLine);
    }
    return super.computeInitialDegressiveDepreciation(fixedAsset, baseValue);
  }

  @Override
  protected BigDecimal getNumberOfPastDepreciation(FixedAsset fixedAsset) {
    return BigDecimal.valueOf(fixedAsset.getImportNbrOfPastDepreciations());
  }

  @Override
  protected BigDecimal getAlreadyDepreciatedAmount(FixedAsset fixedAsset) {

    return fixedAssetLineToolService.getCompanyScaledValue(
        fixedAsset.getImportAlreadyDepreciatedAmount(), fixedAsset);
  }

  @Override
  protected Integer getDurationInMonth(FixedAsset fixedAsset) {

    return fixedAsset.getDurationInMonth();
  }

  @Override
  protected BigDecimal getDepreciatedAmountCurrentYear(FixedAsset fixedAsset) {
    return fixedAssetLineToolService.getCompanyScaledValue(
        fixedAsset.getDepreciatedAmountCurrentYear(), fixedAsset);
  }

  @Override
  protected LocalDate getFailOverDepreciationEndDate(FixedAsset fixedAsset) {
    return fixedAsset.getFailOverDepreciationEndDate();
  }

  @Override
  protected int getFirstDateDepreciationInitSelect(FixedAsset fixedAsset) {
    return fixedAsset.getFirstDepreciationDateInitSelect();
  }
}
