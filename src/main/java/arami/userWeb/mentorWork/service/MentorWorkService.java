package arami.userWeb.mentorWork.service;

import java.util.List;

import arami.userWeb.mentorWork.service.dto.request.MentorWorkListRequest;
import arami.userWeb.mentorWork.service.dto.response.MentorWorkAdviceItem;

/**
 * 멘토업무(mentorWork) 화면 Service.
 */
public interface MentorWorkService {

    List<MentorWorkAdviceItem> getMentorWorkAdviceList(MentorWorkListRequest request);
}
