package arami.common.cert.comm.service;

import arami.common.cert.comm.MobileCertDTO;
import org.egovframe.rte.psl.dataaccess.EgovAbstractMapper;
import org.springframework.stereotype.Repository;

@Repository("mobileCertDAO")
public class MobileCertDAO extends EgovAbstractMapper {

    public int insertMobileCert(MobileCertDTO mobileCertDTO){
        return insert("mobileCertDAO.insertMobileCert", mobileCertDTO);
    }

}
