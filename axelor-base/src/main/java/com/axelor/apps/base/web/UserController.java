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
package com.axelor.apps.base.web;

import com.axelor.app.AppSettings;
import com.axelor.apps.base.ResponseMessageType;
import com.axelor.apps.base.db.Company;
import com.axelor.apps.base.db.Partner;
import com.axelor.apps.base.db.TradingName;
import com.axelor.apps.base.db.repo.CompanyRepository;
import com.axelor.apps.base.db.repo.PartnerRepository;
import com.axelor.apps.base.db.repo.TradingNameRepository;
import com.axelor.apps.base.exceptions.BaseExceptionMessage;
import com.axelor.apps.base.service.exception.TraceBackService;
import com.axelor.apps.base.service.user.UserService;
import com.axelor.auth.AuthUtils;
import com.axelor.auth.db.User;
import com.axelor.auth.db.repo.UserRepository;
import com.axelor.common.StringUtils;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.axelor.rpc.Context;
import com.axelor.utils.helpers.ModelHelper;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Singleton;
import java.util.Map;
import javax.validation.ValidationException;

@Singleton
public class UserController {
  protected static final Map<String, String> UNIQUE_MESSAGES =
      ImmutableMap.of("code", I18n.get(BaseExceptionMessage.USER_CODE_ALREADY_EXISTS));

  public void setUserPartner(ActionRequest request, ActionResponse response) {
    try {
      Context context = request.getContext();
      Partner partner =
          Beans.get(PartnerRepository.class).find(context.asType(Partner.class).getId());

      if (context.get("created_from_user_id") != null && partner != null) {

        UserRepository userRepo = Beans.get(UserRepository.class);
        User user = userRepo.find(((Integer) context.get("created_from_user_id")).longValue());
        Beans.get(UserService.class).setUserPartner(partner, user);
      }
    } catch (Exception e) {
      TraceBackService.trace(response, e, ResponseMessageType.ERROR);
    }
  }

  public void applyApplicationMode(ActionRequest request, ActionResponse response) {
    String applicationMode = AppSettings.get().get("application.mode", "prod");
    if ("dev".equals(applicationMode)) {
      response.setAttr("testingPanel", "hidden", false);
    }
  }

  public void validate(ActionRequest request, ActionResponse response) {
    try {
      Context context = request.getContext();
      User user = request.getContext().asType(User.class);
      Map<String, String> errors = ModelHelper.getUniqueErrors(user, UNIQUE_MESSAGES);

      if (!errors.isEmpty()) {
        response.setErrors(errors);
        return;
      }

      UserService userService = Beans.get(UserService.class);
      user = userService.changeUserPassword(user, context);

      response.setValue("transientPassword", user.getTransientPassword());
    } catch (ValidationException e) {
      response.setError(e.getMessage());
    } catch (Exception e) {
      TraceBackService.trace(response, e);
    }
  }

  public void generateRandomPassword(ActionRequest request, ActionResponse response) {
    try {
      UserService userService = Beans.get(UserService.class);
      CharSequence password = userService.generateRandomPassword();

      response.setValue("newPassword", password);
      response.setValue("chkPassword", password);
    } catch (Exception e) {
      TraceBackService.trace(response, e);
    }
  }

  public void validatePassword(ActionRequest request, ActionResponse response) {
    try {
      UserService userService = Beans.get(UserService.class);
      String newPassword =
          MoreObjects.firstNonNull((String) request.getContext().get("newPassword"), "");
      boolean valid = userService.matchPasswordPattern(newPassword);

      response.setAttr("passwordPatternDescriptionLabel", "hidden", valid);
    } catch (Exception e) {
      TraceBackService.trace(response, e);
    }
  }

  public void setDefaultUserMethod(ActionRequest request, ActionResponse response) {
    try {
      AppSettings appSettings = AppSettings.get();
      response.setValue("language", appSettings.get("application.locale"));
    } catch (Exception e) {
      TraceBackService.trace(response, e);
    }
  }

  public void setActiveCompany(ActionRequest request, ActionResponse response) {
    try {
      Company company = request.getContext().asType(Company.class);
      company = Beans.get(CompanyRepository.class).find(company.getId());
      Beans.get(UserService.class).setActiveCompany(AuthUtils.getUser(), company);
      response.setNotify(
          String.format(I18n.get("Active company changed to %s"), company.getName()));
    } catch (Exception e) {
      TraceBackService.trace(response, e);
    }
  }

  public void setTradingName(ActionRequest request, ActionResponse response) {
    try {
      TradingName tradingName = request.getContext().asType(TradingName.class);
      tradingName = Beans.get(TradingNameRepository.class).find(tradingName.getId());
      Beans.get(UserService.class).setTradingName(AuthUtils.getUser(), tradingName);
      response.setNotify(
          String.format(I18n.get("Trading name changed to %s"), tradingName.getName()));
    } catch (Exception e) {
      TraceBackService.trace(response, e);
    }
  }

  public void validateCode(ActionRequest request, ActionResponse response) {
    try {
      User user = request.getContext().asType(User.class);
      Map<String, String> errors = ModelHelper.getUniqueErrors(user, UNIQUE_MESSAGES);

      String code = user.getCode();
      if (StringUtils.isEmpty(code) || code.length() < 2) {
        errors.put(
            "code", I18n.get(BaseExceptionMessage.USER_CODE_LENGTH_SHOULD_BE_GREATER_THAN_2));
      }
      response.setErrors(errors);
      response.setAttr("okBtn", "readonly", !errors.isEmpty());
    } catch (Exception e) {
      TraceBackService.trace(response, e);
    }
  }
}
