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
package com.axelor.apps.stock.rest;

import com.axelor.apps.base.AxelorException;
import com.axelor.apps.stock.db.StockMoveLine;
import com.axelor.apps.stock.rest.dto.StockMoveLinePutRequest;
import com.axelor.apps.stock.rest.dto.StockMoveLineResponse;
import com.axelor.apps.stock.service.StockMoveLineService;
import com.axelor.apps.stock.translation.ITranslation;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.axelor.utils.api.HttpExceptionHandler;
import com.axelor.utils.api.ObjectFinder;
import com.axelor.utils.api.RequestStructure;
import com.axelor.utils.api.RequestValidator;
import com.axelor.utils.api.ResponseConstructor;
import com.axelor.utils.api.SecurityCheck;
import io.swagger.v3.oas.annotations.Operation;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/aos/stock-move-line")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class StockMoveLineRestController {

  /**
   * Update realQty and conformity of an incoming stock move. Full path to request is
   * /ws/aos/stock-move-line/{id}
   */
  @Operation(
      summary = "Update stock move line",
      tags = {"Stock move line"})
  @Path("/{id}")
  @PUT
  @HttpExceptionHandler
  public Response updateStockMoveLine(
      @PathParam("id") long stockMoveLineId, StockMoveLinePutRequest requestBody)
      throws AxelorException {
    RequestValidator.validateBody(requestBody);
    new SecurityCheck().writeAccess(StockMoveLine.class, stockMoveLineId).check();

    StockMoveLine stockmoveLine =
        ObjectFinder.find(StockMoveLine.class, stockMoveLineId, requestBody.getVersion());

    Beans.get(StockMoveLineService.class)
        .updateStockMoveLine(
            stockmoveLine,
            requestBody.getRealQty(),
            requestBody.getConformity(),
            requestBody.fetchUnit(),
            requestBody.fetchFromStockLocation(),
            requestBody.fetchToStockLocation());

    return ResponseConstructor.build(
        Response.Status.OK,
        I18n.get(ITranslation.STOCK_MOVE_LINE_UPDATED),
        new StockMoveLineResponse(stockmoveLine));
  }

  @Operation(
      summary = "Split stock move line",
      tags = {"Stock move line"})
  @Path("split/{id}")
  @PUT
  @HttpExceptionHandler
  public Response splitStockMoveLine(
      @PathParam("id") long stockMoveLineId, RequestStructure requestBody) throws AxelorException {
    RequestValidator.validateBody(requestBody);
    new SecurityCheck()
        .writeAccess(StockMoveLine.class, stockMoveLineId)
        .createAccess(StockMoveLine.class)
        .check();

    StockMoveLine stockmoveLine =
        ObjectFinder.find(StockMoveLine.class, stockMoveLineId, requestBody.getVersion());

    Beans.get(StockMoveLineService.class)
        .splitIntoFulfilledMoveLineAndUnfulfilledOne(stockmoveLine);

    return ResponseConstructor.build(
        Response.Status.OK,
        I18n.get(ITranslation.STOCK_MOVE_LINE_SPLIT),
        new StockMoveLineResponse(stockmoveLine));
  }

  @Operation(
      summary = "Stock move line quantity availability",
      tags = {"Stock move line"})
  @Path("check-quantity/{stockMoveLineId}")
  @GET
  @HttpExceptionHandler
  public Response checkStockMoveLineQty(@PathParam("stockMoveLineId") long stockMoveLineId)
      throws AxelorException {
    new SecurityCheck().readAccess(StockMoveLine.class, stockMoveLineId).check();
    StockMoveLine stockMoveLine =
        ObjectFinder.find(StockMoveLine.class, stockMoveLineId, ObjectFinder.NO_VERSION);

    return ResponseConstructor.build(
        Response.Status.OK,
        I18n.get(ITranslation.STOCK_MOVE_LINE_QUANTITY_AVAILABILITY),
        Beans.get(StockMoveLineService.class).setAvailableStatus(stockMoveLine));
  }
}
