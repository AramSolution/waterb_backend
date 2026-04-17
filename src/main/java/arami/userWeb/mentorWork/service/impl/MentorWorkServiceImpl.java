package arami.userWeb.mentorWork.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import arami.userWeb.mentorWork.service.MentorWorkDAO;
import arami.userWeb.mentorWork.service.MentorWorkService;
import arami.userWeb.mentorWork.service.dto.request.MentorWorkListRequest;
import arami.userWeb.mentorWork.service.dto.response.MentorWorkAdviceItem;
import lombok.RequiredArgsConstructor;

@Service("mentorWorkService")
@RequiredArgsConstructor
public class MentorWorkServiceImpl implements MentorWorkService {

    private final MentorWorkDAO mentorWorkDAO;

    @Override
    public List<MentorWorkAdviceItem> getMentorWorkAdviceList(MentorWorkListRequest request) {
        return mentorWorkDAO.selectMentorWorkAdviceList(request);
    }
}
