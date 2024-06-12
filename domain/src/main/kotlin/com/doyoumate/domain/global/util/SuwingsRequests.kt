package com.doyoumate.domain.global.util

import com.doyoumate.common.util.getValue
import com.doyoumate.domain.lecture.model.enum.Semester
import com.fasterxml.jackson.databind.JsonNode

object SuwingsRequests {
    fun getProfileRequest(studentNumber: String, year: Int, semester: Semester): String =
        """
           <rqM0_F0 task="system.commonTask" action="comSelect" xda="academic.ar.iframe.ar02_20030201_f_M0_F0_xda" con="enc"> 
               <PGM_ID value="90010101"/>
               <YY value="$year"/>
               <SHTM_CD value="${semester.id}"/>
               <STUNO value="$studentNumber"/>
           </rqM0_F0>
       """

    fun getGpaRequest(studentNumber: String, year: Int, semester: Semester): String =
        """
           <rqM0_F0 task="system.commonTask" action="comSelect" xda="academic.ag.ag04.ag04_20060407_m_M0_F0_xda" con="enc"> 
               <FCLT_GSCH_DIV value="1"/>
               <STUNO value="$studentNumber"/>
               <LSRT_YY value="$year"/>
               <LSRT_SHTM_CD value="${semester.id}"/>
           </rqM0_F0>
        """

    fun getPhoneNumberRequest(studentNumber: String): String =
        """
            <rqM0_F0 task="system.commonTask" action="comSelect" xda="academic.ar.iframe.ar02_20030202_d_M0_F0_xda" con="enc"> 
			    <STUNO value="$studentNumber"/>
            </rqM0_F0>
        """

    fun getRankRequest(studentNumber: String, year: Int, semester: Semester): String =
        """
           <rqM0_F0 task="system.commonTask" action="comSelect" xda="academic.ag.ag02.ag02_20060206_m_M0_F0_xda" con="sudev"> 
               <FCLT_GSCH_DIV_CD value="1"/>
               <YY value="$year"/> 
               <SHTM_CD value="${semester.id}"/> 
               <STUNO value="$studentNumber"/>
           </rqM0_F0>
        """

    fun getLecturesRequest(year: Int, semester: Semester): String =
        """
            <rqM0_F0 task="system.commonTask" action="comSelect" xda="academic.ac.ac03.ac03_20040305_m_M0_F0_xda" con="sudev">
            	<FCLT_GSCH_DIV_CD value="1"/>
            	<OPEN_YY value="$year"/>
            	<OPEN_SHTM_CD value="${semester.id}"/>
            </rqM0_F0>
        """

    fun getAppliedLecturesRequest(studentNumber: String, year: Int, semester: Semester): String =
        """
           <rqM1_F0 task="system.commonTask" action="comSelect" xda="academic.al.al02.al02_20050206_m_M0_F0_xda" con="sudev">
               <FCLT_GSCH_DIV_CD value="1"/>
               <YY value="$year"/>
               <SHTM_CD value="${semester.id}"/>
               <STUNO value="$studentNumber"/>
           </rqM1_F0>
        """

    fun getPreAppliedLecturesRequest(studentNumber: String, year: Int, semester: Semester): String =
        """
           <rqM2_F0 task="system.commonTask" action="comSelect" xda="academic.al.al04.al04_20050403_m_M2_F0_xda" con="sudev">
                <FCLT_GSCH_DIV_CD value="1"/>
                <YY value="$year"/>   
                <SHTM_CD value="${semester.id}"/>   
                <STUNO value="$studentNumber"/>
           </rqM2_F0>
        """

    fun getLectureDetailsRequest(node: JsonNode): String =
        """
           <rqM0_F0 task="system.commonTask" action="comSelect" xda="academic.ac.ac03.ac03_20040302_m_M0_F0_xda" con="sudev">
           	   <FCLT_GSCH_DIV_CD value="1"/> 
               <OPEN_YY value="${node.getValue<String>("OPEN_YY")}" />
               <OPEN_SHTM_CD value="${node.getValue<String>("OPEN_SHTM_CD")}" />
               <LECT_NO value="${node.getValue<String>("LECT_NO")}" />
           </rqM0_F0>
        """

    fun getProfessorRequest(id: String): String =
        """
            <rqSelectUserInfo task="system.commonTask" action="comSelect" xda="system.selectUserInfoXDA" con="sudev">
    			<USER_NO value="$id"/>
    		</rqSelectUserInfo>
        """

    fun getScoreRequest(id: String, year: Int, semester: Semester): String =
        """
           <rqM0_F0 task="system.commonTask" action="comSelect" xda="academic.ac.popup.ac03_20040305_p_M0_F0_xda" con="sudev"> 
				<FCLT_GSCH_DIV_CD value="1"/> 
				<OPEN_YY value="$year"/>
				<OPEN_SHTM_CD value="$semester"/>
				<STF_NO value="$id"/>
		   </rqM0_F0>
        """

    fun getAppliedStudentsRequest(lectureId: String, year: Int, semester: Semester) =
        """
           <rqM6_F0 task="system.commonTask" action="comSelect" xda="academic.ag.ag02.ag02_20060202_m_M6_F0_xda" con="sudev">
                <FCLT_GSCH_DIV_CD value="1" />
                <YY value="$year" />
                <SHTM_CD value="${semester.id}" />
                <LECT_NO value="${lectureId.substring(10)}" />
           </rqM6_F0>
        """

    fun getChapelInformationRequest(studentNumber: String, year: Int, semester: Semester) =
        """
            <rqM0_F0 task="system.commonTask" action="comSelect" xda="academic.ah.ah03.ah03_20100312_m_M0_F0_xda" con="sudev">
				<YY value="$year"/>
				<SHTM_CD value="${semester.id}"/>
				<STUNO value="$studentNumber"/>
			</rqM0_F0>
        """

    fun getChapelAttendanceRequest(studentNumber: String, year: Int, semester: Semester) =
        """
            <rqM0_F1 task="system.commonTask" action="comSelect" xda="academic.ah.ah03.ah03_20100312_m_M0_F1_xda" con="sudev">
				<YY value="$year"/>
				<SHTM_CD value="${semester.id}"/>
				<STUNO value="$studentNumber"/>
			</rqM0_F1>
        """
}
