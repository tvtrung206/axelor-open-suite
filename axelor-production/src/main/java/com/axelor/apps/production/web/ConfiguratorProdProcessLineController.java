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
package com.axelor.apps.production.web;

import com.axelor.apps.base.service.exception.TraceBackService;
import com.axelor.apps.production.db.ConfiguratorProdProcessLine;
import com.axelor.apps.production.db.WorkCenter;
import com.axelor.apps.production.service.WorkCenterService;
import com.axelor.inject.Beans;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;

public class ConfiguratorProdProcessLineController {

  public void updateDuration(ActionRequest request, ActionResponse response) {
    try {
      ConfiguratorProdProcessLine confProdProcessLine =
          request.getContext().asType(ConfiguratorProdProcessLine.class);
      WorkCenter workCenter = confProdProcessLine.getWorkCenter();
      if (workCenter != null) {
        response.setValue("durationPerCycle", workCenter.getDurationPerCycle());
        response.setValue("humanDuration", workCenter.getHrDurationPerCycle());
      }
    } catch (Exception e) {
      TraceBackService.trace(response, e);
    }
  }

  public void updateCapacitySettings(ActionRequest request, ActionResponse response) {
    try {
      ConfiguratorProdProcessLine confProdProcessLine =
          request.getContext().asType(ConfiguratorProdProcessLine.class);
      WorkCenter workCenter = confProdProcessLine.getWorkCenter();
      if (workCenter != null) {
        response.setValue(
            "minCapacityPerCycle",
            Beans.get(WorkCenterService.class).getMinCapacityPerCycleFromWorkCenter(workCenter));
        response.setValue(
            "maxCapacityPerCycle",
            Beans.get(WorkCenterService.class).getMaxCapacityPerCycleFromWorkCenter(workCenter));
      }
    } catch (Exception e) {
      TraceBackService.trace(response, e);
    }
  }

  public void fillWorkCenter(ActionRequest request, ActionResponse response) {
    try {
      ConfiguratorProdProcessLine confProdProcessLine =
          request.getContext().asType(ConfiguratorProdProcessLine.class);
      response.setValue(
          "workCenter",
          Beans.get(WorkCenterService.class)
              .getMainWorkCenterFromGroup(confProdProcessLine.getWorkCenterGroup()));
    } catch (Exception e) {
      TraceBackService.trace(response, e);
    }
  }
}
