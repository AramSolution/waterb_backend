package arami.userWeb.mentorWork.service;

import java.util.List;

import org.egovframe.rte.psl.dataaccess.EgovAbstractMapper;
import org.springframework.stereotype.Repository;

import arami.userWeb.mentorWork.service.dto.request.MentorWorkListRequest;
import arami.userWeb.mentorWork.service.dto.response.MentorWorkAdviceItem;

/**
 * 멘토업무(mentorWork) 화면 DAO.
 * MentorWork_SQL_mysql.xml (namespace: mentorWorkDAO) 사용.
 */
@Repository("mentorWorkDAO")
public class MentorWorkDAO extends EgovAbstractMapper {

    public List<MentorWorkAdviceItem> selectMentorWorkAdviceList(MentorWorkListRequest request) {
        return selectList("mentorWorkDAO.selectMentorWorkAdviceList", request);
    }
}
