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
package com.axelor.apps.quality.db.repo;

import com.axelor.apps.base.AxelorException;
import com.axelor.apps.base.db.repo.SequenceRepository;
import com.axelor.apps.base.service.administration.SequenceService;
import com.axelor.apps.base.service.exception.TraceBackService;
import com.axelor.apps.quality.db.QualityAlert;
import com.google.common.base.Strings;
import com.google.inject.Inject;

public class QualityAlertManagementRepository extends QualityAlertRepository {

  protected SequenceService sequenceService;

  @Inject
  public QualityAlertManagementRepository(SequenceService sequenceService) {
    this.sequenceService = sequenceService;
  }

  /**
   * Generate and set sequence in reference with predefined prefix.
   *
   * @param qualityAlert Overridden quality alert object to set reference on onSave event.
   */
  @Override
  public QualityAlert save(QualityAlert qualityAlert) {
    if (Strings.isNullOrEmpty(qualityAlert.getReference())) {
      try {
        qualityAlert.setReference(
            sequenceService.getSequenceNumber(
                SequenceRepository.QUALITY_ALERT,
                null,
                QualityAlert.class,
                "reference",
                qualityAlert));
      } catch (AxelorException e) {
        TraceBackService.traceExceptionFromSaveMethod(e);
      }
    }
    return super.save(qualityAlert);
  }
}
