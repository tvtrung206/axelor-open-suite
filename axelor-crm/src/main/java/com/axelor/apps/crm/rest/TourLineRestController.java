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
package com.axelor.apps.crm.rest;

import com.axelor.apps.crm.db.TourLine;
import com.axelor.apps.crm.service.TourLineService;
import com.axelor.apps.crm.translation.ITranslation;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.axelor.utils.api.HttpExceptionHandler;
import com.axelor.utils.api.ObjectFinder;
import com.axelor.utils.api.ResponseConstructor;
import com.axelor.utils.api.SecurityCheck;
import io.swagger.v3.oas.annotations.Operation;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/aos/tour-line")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TourLineRestController {
  @Operation(
      summary = "Validate tour line",
      tags = {"Tour line"})
  @Path("/validate/{tourLineId}")
  @PUT
  @HttpExceptionHandler
  public Response validate(@PathParam("tourLineId") Long tourLineId) {
    new SecurityCheck().writeAccess(TourLine.class).createAccess(TourLine.class).check();

    TourLine tourLine = ObjectFinder.find(TourLine.class, tourLineId, ObjectFinder.NO_VERSION);
    Beans.get(TourLineService.class).setValidatedAndLastVisitDate(tourLine);

    return ResponseConstructor.build(
        Response.Status.OK, I18n.get(ITranslation.TOUR_LINE_VALIDATED));
  }
}
