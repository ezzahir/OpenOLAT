/**
 * <a href="http://www.openolat.org">
 * OpenOLAT - Online Learning and Training</a><br>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); <br>
 * you may not use this file except in compliance with the License.<br>
 * You may obtain a copy of the License at the
 * <a href="http://www.apache.org/licenses/LICENSE-2.0">Apache homepage</a>
 * <p>
 * Unless required by applicable law or agreed to in writing,<br>
 * software distributed under the License is distributed on an "AS IS" BASIS, <br>
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. <br>
 * See the License for the specific language governing permissions and <br>
 * limitations under the License.
 * <p>
 * Initial code contributed and copyrighted by<br>
 * frentix GmbH, http://www.frentix.com
 * <p>
 */
package org.olat.modules.organisation.ui;

import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.form.flexible.FormItemContainer;
import org.olat.core.gui.components.form.flexible.elements.RichTextElement;
import org.olat.core.gui.components.form.flexible.elements.TextElement;
import org.olat.core.gui.components.form.flexible.impl.FormBasicController;
import org.olat.core.gui.components.form.flexible.impl.FormLayoutContainer;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.Event;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.util.StringHelper;
import org.olat.modules.organisation.Organisation;
import org.olat.modules.organisation.OrganisationManagedFlag;
import org.olat.modules.organisation.OrganisationService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * Initial date: 9 févr. 2018<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class EditOrganisationController extends FormBasicController {

	private RichTextElement descriptionEl;
	private TextElement identifierEl;
	private TextElement displayNameEl;
	
	private Organisation organisation;
	private Organisation parentOrganisation;
	
	@Autowired
	private OrganisationService organisationService;
	
	public EditOrganisationController(UserRequest ureq, WindowControl wControl, Organisation organisation) {
		super(ureq, wControl);
		parentOrganisation = null;
		this.organisation = organisation;
		initForm(ureq);
	}
	
	public EditOrganisationController(UserRequest ureq, WindowControl wControl, Organisation organisation, Organisation parentOrganisation) {
		super(ureq, wControl);
		this.organisation = organisation;
		this.parentOrganisation = parentOrganisation;
		initForm(ureq);
	}
	
	public Organisation getOrganisation() {
		return organisation;
	}

	@Override
	protected void initForm(FormItemContainer formLayout, Controller listener, UserRequest ureq) {
		if(organisation != null) {
			String key = organisation.getKey().toString();
			uifactory.addStaticTextElement("organisation.key", key, formLayout);
			String externalId = organisation.getExternalId();
			uifactory.addStaticTextElement("organisation.external.id", externalId, formLayout);
		}
		
		String identifier = organisation == null ? "" : organisation.getIdentifier();
		identifierEl = uifactory.addTextElement("organisation.identifier", "organisation.identifier", 255, identifier, formLayout);
		identifierEl.setEnabled(!OrganisationManagedFlag.isManaged(organisation, OrganisationManagedFlag.identifier));
		identifierEl.setMandatory(true);

		String displayName = organisation == null ? "" : organisation.getDisplayName();
		displayNameEl = uifactory.addTextElement("organisation.displayName", "organisation.displayName", 255, displayName, formLayout);
		displayNameEl.setEnabled(!OrganisationManagedFlag.isManaged(organisation, OrganisationManagedFlag.displayName));
		displayNameEl.setMandatory(true);
		
		String description = organisation == null ? "" : organisation.getDescription();
		descriptionEl = uifactory.addRichTextElementForStringDataCompact("organisation.description", "organisation.description", description, 10, 60, null,
				formLayout, ureq.getUserSession(), getWindowControl());
		descriptionEl.setEnabled(!OrganisationManagedFlag.isManaged(organisation, OrganisationManagedFlag.description));
		
		FormLayoutContainer buttonsCont = FormLayoutContainer.createButtonLayout("buttons", getTranslator());
		formLayout.add(buttonsCont);
		uifactory.addFormCancelButton("cancel", buttonsCont, ureq, getWindowControl());
		uifactory.addFormSubmitButton("save", buttonsCont);
	}

	@Override
	protected void doDispose() {
		//
	}

	@Override
	protected boolean validateFormLogic(UserRequest ureq) {
		boolean allOk = true;
		
		displayNameEl.clearError();
		if(!StringHelper.containsNonWhitespace(displayNameEl.getValue())) {
			displayNameEl.setErrorKey("form.legende.mandatory", null);
			allOk &= false;
		}
		
		identifierEl.clearError();
		if(!StringHelper.containsNonWhitespace(identifierEl.getValue())) {
			identifierEl.setErrorKey("form.legende.mandatory", null);
			allOk &= false;
		}
		
		return allOk & super.validateFormLogic(ureq);
	}

	@Override
	protected void formOK(UserRequest ureq) {
		if(organisation == null) {
			//create a new one
			organisation = organisationService
					.createOrganisation(identifierEl.getValue(), displayNameEl.getValue(), descriptionEl.getValue(), parentOrganisation, null);
		} else {
			organisation = organisationService.getOrganisation(organisation);
			organisation.setIdentifier(identifierEl.getValue());
			organisation.setDisplayName(displayNameEl.getValue());
			organisation.setDescription(descriptionEl.getValue());
			organisation = organisationService.updateOrganisation(organisation);
		}

		fireEvent(ureq, Event.DONE_EVENT);
	}

	@Override
	protected void formCancelled(UserRequest ureq) {
		fireEvent(ureq, Event.CANCELLED_EVENT);
	}
}