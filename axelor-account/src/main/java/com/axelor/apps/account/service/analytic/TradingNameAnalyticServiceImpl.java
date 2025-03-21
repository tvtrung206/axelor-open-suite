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
package com.axelor.apps.account.service.analytic;

import com.axelor.apps.account.db.repo.AccountConfigRepository;
import com.axelor.apps.account.service.config.AccountConfigService;
import com.axelor.apps.base.AxelorException;
import com.axelor.apps.base.db.Company;
import com.axelor.apps.base.db.TradingName;
import com.axelor.common.ObjectUtils;
import com.google.inject.Inject;

public class TradingNameAnalyticServiceImpl implements TradingNameAnalyticService {

  protected AccountConfigService accountConfigService;

  @Inject
  public TradingNameAnalyticServiceImpl(AccountConfigService accountConfigService) {
    this.accountConfigService = accountConfigService;
  }

  @Override
  public String getDomainOnCompany(TradingName tradingName) {
    Company company = tradingName.getCompany();
    String domain =
        !ObjectUtils.isEmpty(company)
            ? "self.company = " + company.getId() + " AND self.isSpecific = false"
            : "self.id = 0";
    return domain;
  }

  @Override
  public boolean isAnalyticTypeByTradingName(TradingName tradingName) {
    Company company = tradingName.getCompany();
    if (company == null) {
      return false;
    }
    try {
      return accountConfigService.getAccountConfig(company).getAnalyticDistributionTypeSelect()
          == AccountConfigRepository.DISTRIBUTION_TYPE_TRADING_NAME;
    } catch (AxelorException e) {
      return false;
    }
  }
}
