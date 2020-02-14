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
package org.olat.modules.grading;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.olat.basesecurity.IdentityRef;
import org.olat.core.id.Identity;
import org.olat.core.id.Roles;
import org.olat.core.util.mail.MailerResult;
import org.olat.modules.assessment.AssessmentEntry;
import org.olat.modules.grading.model.GraderWithStatistics;
import org.olat.modules.grading.model.GradersSearchParameters;
import org.olat.modules.grading.model.GradingAssignmentSearchParameters;
import org.olat.modules.grading.model.GradingAssignmentWithInfos;
import org.olat.modules.grading.model.GradingSecurity;
import org.olat.modules.grading.model.ReferenceEntryWithStatistics;
import org.olat.modules.grading.ui.component.GraderMailTemplate;
import org.olat.repository.RepositoryEntry;
import org.olat.repository.RepositoryEntryRef;
import org.olat.user.AbsenceLeave;

/**
 * 
 * Initial date: 13 janv. 2020<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public interface GradingService {
	
	public GradingSecurity isGrader(IdentityRef identity, Roles roles);
	
	public RepositoryEntryGradingConfiguration getOrCreateConfiguration(RepositoryEntry entry);
	
	public RepositoryEntryGradingConfiguration updateConfiguration(RepositoryEntryGradingConfiguration configuration);
	
	public boolean isGradingEnabled(RepositoryEntryRef entry, String softKey);
	
	/**
	 * 
	 * @param entry A test entry (optional)
	 * @return A set of test entries where the flag anonymous is set
	 */
	public Map<Long,GradingAssessedIdentityVisibility> getIdentityVisibility(Collection<RepositoryEntryRef> entries);

	public GradingAssessedIdentityVisibility getIdentityVisibility(RepositoryEntryRef entry);
	
	public void addGraders(RepositoryEntry entry, List<Identity> identities, GraderMailTemplate template, MailerResult mailerResult);

	public List<GraderToIdentity> getGraders(RepositoryEntry entry);
	
	public List<AbsenceLeave> getGradersAbsenceLeaves(RepositoryEntry entry);
	
	public List<GraderWithStatistics> getGradersWithStatistics(GradersSearchParameters searchParams);
	
	public List<ReferenceEntryWithStatistics> getGradedEntriesWithStatistics(Identity grader);

	/**
	 * Activate the specified identity as grader for all reference / test entry where it was
	 * deactivated.
	 * 
	 * @param identity The grader's identity
	 */
	public void activateGrader(Identity identity);
	
	/**
	 * Activate the specified identity as grader for the specified reference / test entry.
	 * 
	 * @param entry The reference / test entry
	 * @param identity The grader's identity
	 */
	public void activateGrader(RepositoryEntry entry, Identity identity);


	/**
	 * Deactivate the grader for all repository entries where it's active.
	 * 
	 * @param identity The identity to deactivate
	 * @param replacementGrader A replacement for the deactivated identity as grader
	 * @param reassignmentTemplate The mail template to announce the replacement
	 * @param result Result informations of mail
	 */
	public void deactivateGrader(Identity identity, Identity replacementGrader, GraderMailTemplate reassignmentTemplate, MailerResult result);
	
	/**
	 * Deactivate the grader for the specified and only the specified repository entry.
	 * 
	 * @param entry The reference / test entry
	 * @param identity The identity to deactivate
	 * @param replacementGrader A replacement for the deactivated identity as grader
	 * @param reassignmentTemplate The mail template to announce the replacement
	 * @param result Result informations of mail
	 */
	public void deactivateGrader(RepositoryEntry entry, Identity identity, Identity replacementGrader, GraderMailTemplate reassignmentTemplate, MailerResult result);
	
	/**
	 * Remove the specified identity of all its reference / test entries as a grader.
	 * 
	 * @param identity The identity to remove
	 * @param replacementGrader A replacement for the removed identity as grader
	 * @param reassignmentTemplate The mail template to announce the replacement
	 * @param result Result informations of mail
	 */
	public void removeGrader(Identity identity, Identity replacementGrader, GraderMailTemplate reassignmentTemplate, MailerResult result);
	
	/**
	 * Remove the grader for the specified and only the specified repository entry.
	 * 
	 * @param entry The reference / test entry
	 * @param identity The identity to remove
	 * @param replacementGrader A replacement for the removed identity as grader
	 * @param reassignmentTemplate The mail template to announce the replacement
	 * @param result Result informations of mail
	 */
	public void removeGrader(RepositoryEntry entry, Identity identity, Identity replacementGrader, GraderMailTemplate reassignmentTemplate, MailerResult result);
	
	/**
	 * Return the assignment with as much data fetched as possible: grader identity,
	 * assessed identity, course entry, test entry...
	 * 
	 * @param ref The assignment reference
	 * @return A fully loaded assignment
	 */
	public GradingAssignment getGradingAssignment(GradingAssignmentRef ref);
	
	public GradingAssignment getGradingAssignment(RepositoryEntryRef testEntry, AssessmentEntry entry);
	
	public List<GradingAssignmentWithInfos> getGradingAssignmentsWithInfos(GradingAssignmentSearchParameters searchParams);
	
	public void assignGrader(RepositoryEntry referenceEntry, AssessmentEntry assessmentEntry, boolean updateAssessmentDate);
	

	public GradingAssignment extendAssignmentDeadline(GradingAssignment assignment, Date newDeadline);
	
	public GradingAssignment assignGrader(GradingAssignment assignment, Identity grader, GraderMailTemplate template, MailerResult mailerResult);
	
	public GradingAssignment unassignGrader(GradingAssignment assignment);
	
	public GradingAssignment assignmentDone(GradingAssignment assignment);
	
	public GradingAssignment reopenAssignment(GradingAssignment assignment);
	

	public void updateDeadline(RepositoryEntry referenceEntry, RepositoryEntryGradingConfiguration configuration);
	
	/**
	 * Return a list of repository entries with grading enabled and
	 * that the specified user can access as owner, learn resource
	 * manager, principal or administrator.
	 * 
	 * @param identity The user which want to access the list
	 * @return A list of entries (nothing fetched)
	 */
	public List<RepositoryEntry> getReferenceRepositoryEntriesWithGrading(Identity identity);
	
	/**
	 * Return a list of repository entries with grading enabled and
	 * where the specified identity is grader.
	 * 
	 * @param identity The grader which want to access the list
	 * @return A list of entries (nothing fetched)
	 */
	public List<RepositoryEntry> getReferenceRepositoryEntriesAsGrader(IdentityRef grader);
	
	/**
	 * Typically the courses which have this test for
	 * grading purpose.
	 * 
	 * @param referenceEntry The reference entry
	 * @return A list of repository entries (nothing fetched)
	 */
	public List<RepositoryEntry> getEntriesWithGrading(RepositoryEntryRef referenceEntry);
	
	/**
	 * Typically the courses which have a test the specified
	 * user can see as owner, learn resource manager, administrator
	 * or principal.
	 * 
	 * @param identity The user
	 * @return A list of repository entries (nothing fetched)
	 */
	public List<RepositoryEntry> getEntriesWithGrading(IdentityRef identity);
	
	
	/**
	 * Return a list of identities which are graders in a resource
	 * with grading enabled and that the specified user can access
	 * as owner, learn resource manager, principal or administrator.
	 * 
	 * @param identity The user which want to access the list
	 * @return A list of identities
	 */
	public List<Identity> getGraders(Identity identity);
	
	public AssessmentEntry loadFullAssessmentEntry(AssessmentEntry assessmentEntry);
	
	/**
	 * Retrieve the time record for the specified assignment
	 * with the grader linked to the assignment.
	 * 
	 * @param assignment The assignment
	 * @return A reference of the record
	 */
	public GradingTimeRecordRef getCurrentTimeRecord(GradingAssignment assignment);
	
	public void appendTimeTo(GradingTimeRecordRef record, long addedTime, TimeUnit unit);
	
	public String getCachedCourseElementTitle(RepositoryEntry entry, String subIdenty);
	
	public void sendReminders();


}
