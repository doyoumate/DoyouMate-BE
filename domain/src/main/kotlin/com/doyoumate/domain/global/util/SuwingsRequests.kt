package com.doyoumate.domain.global.util

import com.doyoumate.common.util.getValue
import com.doyoumate.domain.lecture.model.enum.Semester
import com.fasterxml.jackson.databind.JsonNode
import java.time.LocalDate

object SuwingsRequests {
    fun getProfileRequest(studentNumber: String): String =
        LocalDate.now()
            .let {
                """
                    <rqM0_F0 task="system.commonTask" action="comSelect" xda="academic.ar.iframe.ar02_20030201_f_M0_F0_xda" con="enc"> 
            	        <PGM_ID value="90010101"/>
            	        <YY value="${it.year}"/>
            	        <SHTM_CD value="${Semester(it).id}"/>
            	        <LANG_GUBUN value="K"/>
            	        <STUNO value="$studentNumber"/>
                    </rqM0_F0>
                 """
            }

    fun getGpaRequest(studentNumber: String): String =
        LocalDate.now()
            .let {
                """
                    <rqM0_F0 task="system.commonTask" action="comSelect" xda="academic.ag.ag04.ag04_20060407_m_M0_F0_xda" con="enc"> 
            	        <FCLT_GSCH_DIV value="1"/>
            	        <STUNO value="$studentNumber"/>
            	        <LSRT_YY value="${it.year}"/>
                        <LSRT_SHTM_CD value="${Semester(it).id}"/>
                    </rqM0_F0>
                """
            }

    fun getPhoneNumberRequest(studentNumber: String): String =
        """
            <rqM0_F0 task="system.commonTask" action="comSelect" xda="academic.ar.iframe.ar02_20030202_d_M0_F0_xda" con="enc"> 
			    <STUNO value="$studentNumber"/>
            </rqM0_F0>
        """

    fun getRankRequest(studentNumber: String): String =
        LocalDate.now()
            .let {
                if (it.monthValue > 7) it.year to Semester.FIRST
                else it.year - 1 to Semester.SECOND
            }
            .let { (year, semester) ->
                """
                    <rqM0_F0 task="system.commonTask" action="comSelect" xda="academic.ag.ag02.ag02_20060206_m_M0_F0_xda" con="sudev"> 
            	        <FCLT_GSCH_DIV_CD value="1"/>
            	        <YY value="$year"/> 
            	        <SHTM_CD value="${semester.id}"/> 
            	        <STUNO value="$studentNumber"/>
                    </rqM0_F0>
                """
            }

    fun getLecturesRequest(year: Int, semester: Semester): String =
        """
            <rqM0_F0 task="system.commonTask" action="comSelect" xda="academic.ac.ac03.ac03_20040305_m_M0_F0_xda" con="sudev">
            	<FCLT_GSCH_DIV_CD value="1"/>
            	<OPEN_YY value="$year"/>
            	<OPEN_SHTM_CD value="${semester.id}"/>
            	<LANG_GUBUN value="K"/>
            </rqM0_F0>
        """

    fun getAppliedLecturesRequest(studentNumber: String): String =
        LocalDate.now()
            .let {
                """
                    <rqM2_F0 task="system.commonTask" action="comSelect" xda="academic.al.al04.al04_20050409_m_M2_F0_xda" con="sudev">
                        <FCLT_GSCH_DIV_CD value="1"/>
                        <YY value="${it.year}"/>   
                        <SHTM_CD value="${Semester(it).id}"/>   
                        <STUNO value="$studentNumber"/>
                    </rqM2_F0>
                """
            }

    fun getPreAppliedLecturesRequest(studentNumber: String): String =
        LocalDate.now()
            .let {
                """
                    <rqM2_F0 task="system.commonTask" action="comSelect" xda="academic.al.al04.al04_20050403_m_M2_F0_xda" con="sudev">
                        <FCLT_GSCH_DIV_CD value="1"/>
                        <YY value="${it.year}"/>   
                        <SHTM_CD value="${Semester(it).id}"/>   
                        <STUNO value="$studentNumber"/>
                    </rqM2_F0>
                """
            }

    fun getPlanRequest(node: JsonNode): String =
        """
            <rqM0_F0 task="system.commonTask" action="comSelect" xda="academic.ac.ac05.ac05_20040501_m_M0_F0_xda" con="sudev">
				<FCLT_GSCH_DIV_CD value="1"/> 
				<YY value="${node.getValue<String>("OPEN_YY")}"/> 
				<SHTM_CD value="${node.getValue<String>("OPEN_SHTM_CD")}"/> 
				<LECT_NO value="${node.getValue<String>("LECT_NO")}"/>
                <EMP_NO value="${node.getValue<String>("STF_NO")}"/>
				<EDUCUR_CORS_NO value="${node.getValue<String>("EDUCUR_CORS_NO")}"/>
            </rqM0_F0>
        """

    fun getProfessorRequest(id: String): String =
        """
            <rqSelectUserInfo task="system.commonTask" action="comSelect" xda="system.selectUserInfoXDA" con="sudev">
    			<USER_NO value="$id"/>
    		</rqSelectUserInfo>
        """

    fun getScoreRequest(id: String): String =
        LocalDate.now()
            .let {
                """
                    <rqM0_F0 task="system.commonTask" action="comSelect" xda="academic.ac.popup.ac03_20040305_p_M0_F0_xda" con="sudev"> 
				        <FCLT_GSCH_DIV_CD value="1"/> 
				        <OPEN_YY value="${it.year}"/>
				        <OPEN_SHTM_CD value="${Semester(it).id}"/>
				        <STF_NO value="$id"/>
			        </rqM0_F0>
                """
            }

}
