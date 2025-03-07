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

import com.axelor.apps.account.db.repo.AnalyticLine;
import com.axelor.apps.base.AxelorException;
import com.axelor.apps.base.db.Company;
import com.google.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class AnalyticGroupServiceImpl implements AnalyticGroupService {

  protected AnalyticAttrsService analyticAttrsService;
  protected AnalyticLineService analyticLineService;

  @Inject
  public AnalyticGroupServiceImpl(
      AnalyticAttrsService analyticAttrsService, AnalyticLineService analyticLineService) {
    this.analyticAttrsService = analyticAttrsService;
    this.analyticLineService = analyticLineService;
  }

  @Override
  public Map<String, Map<String, Object>> getAnalyticAxisDomainAttrsMap(
      AnalyticLine analyticLineModel, Company company) throws AxelorException {
    Map<String, Map<String, Object>> attrsMap = new HashMap<>();

    analyticAttrsService.addAnalyticAxisDomains(analyticLineModel, company, attrsMap);

    return attrsMap;
  }

  @Override
  public Map<String, Object> getAnalyticAccountValueMap(AnalyticLine analyticLine, Company company)
      throws AxelorException {
    Map<String, Object> valuesMap = new HashMap<>();

    analyticLineService.setAnalyticAccount(analyticLine, company);

    valuesMap.put("axis1AnalyticAccount", analyticLine.getAxis1AnalyticAccount());
    valuesMap.put("axis2AnalyticAccount", analyticLine.getAxis2AnalyticAccount());
    valuesMap.put("axis3AnalyticAccount", analyticLine.getAxis3AnalyticAccount());
    valuesMap.put("axis4AnalyticAccount", analyticLine.getAxis4AnalyticAccount());
    valuesMap.put("axis5AnalyticAccount", analyticLine.getAxis5AnalyticAccount());

    return valuesMap;
  }
}
