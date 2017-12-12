package com.cg.uas.controller;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;





















import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;


























import com.cg.uas.bean.ApplicationBean;
import com.cg.uas.bean.ProgramsScheduledBean;
import com.cg.uas.bean.ProgramsOfferedBean;
import com.cg.uas.bean.ProgramsRegistered;
import com.cg.uas.bean.UserBean;
import com.cg.uas.exception.UASException;
import com.cg.uas.service.IAdminService;
import com.cg.uas.service.IApplicantService;
import com.cg.uas.service.IMACService;
import com.cg.uas.service.IProgramsOffered;
import com.cg.uas.service.IScheduledPrograms;


	@Controller
	public class UasController {
		
		@Autowired
		IProgramsOffered programService;
		
		@Autowired
		IScheduledPrograms scheduledService;
		
		@Autowired
		IAdminService adminservice;
		
		@Autowired
		IMACService macservice;
		
		@Autowired
		IApplicantService application;
	
		/*
		 * Home Page
		 */
	@RequestMapping("/showHomePage")
	public String showpage()
	{
		
		return"black";
	}
	
	@RequestMapping("/show.obj")
	public ModelAndView showProgramsOffered(){
		
		ProgramsOfferedBean program=new ProgramsOfferedBean();
		
		return new ModelAndView("programsOffered","program",program);
		
	}
	
	
	/*
	 * Login page for admin and mac users
	 */
	@RequestMapping("/showlogin")
	public String showlogin()
	{
		
		return"index";
	}
	
	/*
	 * Checks user login details
	 */
	@RequestMapping(value="/login.obj", method = RequestMethod.POST)
	public ModelAndView checkLogin(@RequestParam("user")String id,@RequestParam("password")String password,HttpSession session) {
		
		
		try {
			
			UserBean user=adminservice.checkuser(id, password);

			if(user==null)
			{
			
				return new ModelAndView("error","message","USER NOT FOUND");
			}
			else 
			{
				if("admin".equals(user.getRole()))
				{
	
					session.setAttribute("user", "admin");
			
					return new ModelAndView("AdminHome");
					
					
				}
					else
					{
			
						return new ModelAndView("MACHome","user","MAC");
					}
			}
		
		} catch (UASException e) {
		
			return new ModelAndView("error","message",e.getMessage());
		}
			
	}
	
	
	/*
	 * logout Page
	 */
	@RequestMapping("/logout")
	public ModelAndView logout(HttpSession session)
	{
		session.setAttribute("user", "notthere");
		ModelAndView model = new ModelAndView("black");
		
		return model;	
	}
	
	
	/*
	 * Offered Programs are added by admin user
	 */
	@RequestMapping("/addPrograms")
	public ModelAndView addProgram(@ModelAttribute("program") ProgramsOfferedBean program,BindingResult result)
	{
		ModelAndView model=new ModelAndView();
		if(result.hasErrors())
		{
			model.setViewName("error");
			model.addObject("message", "Binding Error");
		}
		else
		{
			try {
				adminservice.addProgram(program);
				model.setViewName("success");
				model.addObject("message", "Program Added Successfully !!!");
			} catch (UASException e) {
				// TODO Auto-generated catch block
				model.setViewName("error");
				model.addObject("message", e.getMessage());
			}
			
		}
		return model;
		
	}
	
	
	/*
	 * view all the list of Programs Offered
	 */
	@RequestMapping("/viewAll")
	public ModelAndView viewAllProgramsOffered(){
		
		ModelAndView model;
		
		
		List<ProgramsOfferedBean> list=programService.viewAllProgramsOffered();
		List<ProgramsScheduledBean> list1=scheduledService.viewAllScheduledPrograms();
		List<ProgramsRegistered> list2 = new ArrayList<ProgramsRegistered>();
		
		if(list1.size()<1)
		{
			for(ProgramsOfferedBean po : list)
			{
						ProgramsRegistered pr = new ProgramsRegistered();
						pr.setProgramID(0);
						pr.setProgramName(po.getProgramName());
						pr.setApplicantEligibility(po.getApplicantEligibility());
						pr.setDegreeCertificate(po.getDegreeCertificate());
						pr.setDescription(po.getDescription());
						pr.setDuration(po.getDuration());
						pr.setCheckprogram(false);
						
						list2.add(pr);
					}
			
		}
		else
		{
			int p =list1.size();
			for(ProgramsOfferedBean po : list)
			{
				int n=1;
				for(ProgramsScheduledBean ps :list1)
				{
					
					if(po.getProgramName().equals(ps.getProgramName()))
					{
						ProgramsRegistered pr = new ProgramsRegistered();
						pr.setProgramID(ps.getProgramId());
						pr.setProgramName(po.getProgramName());
						pr.setApplicantEligibility(po.getApplicantEligibility());
						pr.setDegreeCertificate(po.getDegreeCertificate());
						pr.setDescription(po.getDescription());
						pr.setDuration(po.getDuration());
						pr.setCheckprogram(true);
						list2.add(pr);
					}
					else
					{
						if(n==p)
						{

							ProgramsRegistered pr = new ProgramsRegistered();
							pr.setProgramID(ps.getProgramId());
							pr.setProgramName(po.getProgramName());
							pr.setApplicantEligibility(po.getApplicantEligibility());
							pr.setDegreeCertificate(po.getDegreeCertificate());
							pr.setDescription(po.getDescription());
							pr.setDuration(po.getDuration());
							pr.setCheckprogram(false);
							list2.add(pr);	
						}	
					}
					
				n++;}
			}	
		}
		
		
		if(list.size()<1 && list1.size()<1)
		{
			model = new ModelAndView("error");
			model.addObject("message", "No records found");
			return model;
		}
		else
		{
			model=new ModelAndView("viewAllProgramsOffered");
			model.addObject("programsList",list2);
			return model;
		}
	}
	
	
	/*
	 * Update/Delete Programs Offered
	 */

	@RequestMapping("/updateDelete")
	public ModelAndView viewUpdate()
	{
		List<ProgramsOfferedBean> list=programService.viewAllProgramsOffered();
		ModelAndView model;
		if(list.size()<1)
		{
			 model = new ModelAndView("error");
			model.addObject("message", "No records found");
			return model;
		}
		else{

			model = new ModelAndView("updateDelete");
			model.addObject("list", list);
			return model;
		}
				
	}
	
	/*
	 * Schedules the Program
	 */
	@RequestMapping("/schedule")
	public ModelAndView schedule(@RequestParam("programName") String programName)
	{
		ModelAndView model=new ModelAndView("scheduleProgram");
		model.addObject("schedule", programName);
		
		return model;
	}
	/*
	 * Shows the list of scheduled program
	 */
	@RequestMapping("/showScheduleProgram")
	public ModelAndView addSchedule(){
		ModelAndView model = new ModelAndView();
		List<ProgramsOfferedBean> pOffered;
		try {
			pOffered = adminservice.viewAllProgramsOffered();
			if(pOffered.size()<1)
			{
				model.setViewName("error");
				model.addObject("message", "no record found");
				
			}
			else
			{
				model.setViewName("viewPrograms");
				model.addObject("pOffered",pOffered);	
			}
			
		} catch (UASException e) {
			model.setViewName("error");
			model.addObject("message", e.getMessage());
				}
		return model;
	}
	/*
	 * Adds the program to schedule program
	 */

	@RequestMapping("/addSchedule")
	public ModelAndView addScheduleProgram(@ModelAttribute("program") ProgramsScheduledBean schedulebean,BindingResult result){
		ModelAndView model;
		if(result.hasErrors()){
			model=new ModelAndView();
		model.setViewName("error");
		model.addObject("message","binding error");
		}
		else{
			try {
				schedulebean=adminservice.addSchedule(schedulebean);
				model=new ModelAndView("Message");
			} catch (UASException e) {
				model=new ModelAndView();
				model.setViewName("error");
				model.addObject("message", e.getMessage());
			}
		
		}
		return model;
	}
	
	/*
	 * Fetching the single schedule program based on id from schedule programs
	 */
	@RequestMapping("/demo")
	public ModelAndView fetchSingleProgram(@RequestParam("id") int id){
		ProgramsScheduledBean scheduledBean = null;
		try {
		
			
			ModelAndView model;
			scheduledBean = application.viewScheduledProgram(id);
			if(scheduledBean==null)
			{
				model=new ModelAndView("error");
				model.addObject("message", "NO RECORDS FOUND");
			}
			else
			{
				model=new ModelAndView("demo");
				model.addObject("programsList",scheduledBean);
			}
			return model;
		} catch (UASException e) {
			ModelAndView model=new ModelAndView("error");
			model.addObject("message", e.getMessage());
			return model;
		}
		
	}
	/*
	 * Deletes the program from Programs Offered
	 */
	@RequestMapping("/delete")
	public ModelAndView deleteProgram(@RequestParam("programName") String name)
	{
		
		ModelAndView model;
		try {
			adminservice.deleteoffered(name);
			model = new ModelAndView("deleted");
		} catch (UASException e) {
			model = new ModelAndView("error");
			model.addObject("message", e.getMessage());
			
		}
		return model;
		
	}
	/*
	 * Finds the offered program based on name which is to be updated
	 */
	@RequestMapping("/update")
	public ModelAndView updateProgram(@RequestParam("programName") String name)
	{
		ModelAndView model;
		try {
			model =  new ModelAndView("updateoffered");
			model.addObject("offered",adminservice.findoffered(name) );
		} catch (UASException e) {
			model =  new ModelAndView("error");
			model.addObject("message", e.getMessage());
		}
	return model;
		
	}
	/*
	 * Updates the program from Programs Offered
	 */
	@RequestMapping("/updateOffered")
	public ModelAndView updateofferedProgram(@ModelAttribute("program") ProgramsOfferedBean bean)
	{
		
		try {
			return new ModelAndView("updated","offered",adminservice.updateoffered(bean));
		} catch (UASException e) {
			// TODO Auto-generated catch block
			return new ModelAndView("error","message",e.getMessage());
		}
		
	}	
	

	/*
	 * Apply for the program
	 */
	@RequestMapping("/apply")
	public ModelAndView applyprogram(@RequestParam("programid") int id){
		ModelAndView model=new ModelAndView("addapplicant");
		model.addObject("id",id);
		return model;
	}
	
	/*
	 * Shows the list of scheduled programs
	 */
	@RequestMapping("/showScheduledPrograms")
	public ModelAndView showSchedule(){
	
		ModelAndView model=new ModelAndView();
		List<ProgramsScheduledBean> list;
		try {
			list = macservice.viewAllScheduledPrograms();
			if(list.size()<1)
			{
				model.setViewName("error");
				model.addObject("message", "NO RECORDS FOUND");
			}
			else
			{
				model.setViewName("showSchedule");
				
				model.addObject("scheduleList",list );
			}
			
		} catch (UASException e) {
			model.setViewName("error");
			model.addObject("message", e.getMessage());
		}
		
		return model;
	}
	
	/*
	 * viewing the list of schedule programs
	 */
	@RequestMapping("/scheduledPrograms")
	public ModelAndView showScheduledPrograms(){
	
		ModelAndView model=new ModelAndView();
		List<ProgramsScheduledBean> list;
		try {
			list = macservice.viewAllScheduledPrograms();
			if(list.size()<1)
			{
				model.setViewName("error");
				model.addObject("message", "NO RECORDS FOUND");
			}
			else
			{
				model.setViewName("scheduledPrograms");
				
				model.addObject("scheduleList",list );
			}
			
		} catch (UASException e) {
			model.setViewName("error");
			model.addObject("message", e.getMessage());
		}
		
		return model;
	}
	
	/*
	 * Shows the list of applicant details based on id
	 */

	@RequestMapping("/search")
	public ModelAndView applicantsList(@RequestParam("programId") String programId){
		ModelAndView model=new ModelAndView();
		List<ApplicationBean> list;
		try {
			list = macservice.viewApplicant(programId);
			if(list.size()<1)
			{
				model.setViewName("error");
				model.addObject("message", "NO RECORDS FOUND");
			}
			else
			{
				model.setViewName("viewApplicants");
				model.addObject("applicantList", list);
			}
		} catch (UASException e) {
			// TODO Auto-generated catch block
			model.setViewName("error");
			model.addObject("message", e.getMessage());
		}
		
		return model;
	}
	
	/*
	 * Applicant status can be viewed
	 */
	@RequestMapping("/searchById")
	public ModelAndView applicants(@RequestParam("programId") String programId){
		ModelAndView model=new ModelAndView();
		List<ApplicationBean> list;
		try {
			list = macservice.viewApplicant(programId);
			if(list.size()<1)
			{
				model.setViewName("error");
				model.addObject("message", "NO RECORDS FOUND");
			}
			else
			{
				model.setViewName("showApplicants");
				model.addObject("applicantList", list);
			}
		} catch (UASException e) {
			// TODO Auto-generated catch block
			model.setViewName("error");
			model.addObject("message", e.getMessage());
		}
		
		return model;
	}
	
	/*
	 * Programs in a particular time period will be retrieved
	 */
	@RequestMapping("/viewprogramsbydate")
	public ModelAndView programslist(){
		ModelAndView model=new ModelAndView();
		model.setViewName("getprogramsbydate");
		return model;
	}
	
	/*
	 * Shows the Scheduled program details based on the given date
	 */
	@RequestMapping("/getschedulesbydate")
	public ModelAndView getAllScheduledPrograms(@RequestParam("StartDate") Date startDate,@RequestParam("EndDate") Date endDate ){
		ModelAndView model=new ModelAndView();
		List<ProgramsScheduledBean> list;
		try {
			
			list = adminservice.getschedulesBydate(startDate,endDate);
			if(list.size()<1)
			{
				model.setViewName("error");
				model.addObject("message", "NO RECORDS FOUND");
			}
			else
			{
				model.setViewName("showScheduleByDate");
				model.addObject("scheduleList",list);
			}
		} catch (UASException e) {
			
			model.setViewName("error");
			model.addObject("message",e.getMessage());
		
		}
		
		
		return model;
	}
	
	/*
	 * Shows the list of Scheduled programs to be deleted
	 */
	@RequestMapping("/deleteschedule")
	public ModelAndView deleteScheduledPrograms(){
		ModelAndView model=new ModelAndView();
		List<ProgramsScheduledBean> list;
		try {
			list = adminservice.getAllScheduledPrograms();
			if(list.size()<1)
			{
				model.setViewName("error");
				model.addObject("message", "NO RECORDS FOUND");
			}
			else
			{
				model.addObject("schedulelist",list);
				model.setViewName("deleteScheuldes");
			}
		} catch (UASException e) {
			model.setViewName("error");
			model.addObject("message",e.getMessage());
		}
		
		
		return model;
	}
	
	/*
	 * Deletes the program from Programs Offered and shows the list
	 */
	@RequestMapping("/scheduleDelete")
	public ModelAndView deleteScheduled(@RequestParam("programId")int scheduleId){
		ModelAndView model=new ModelAndView();
		try {
			boolean check = adminservice.deleteScheduledPrograms(scheduleId);
			if(check)
			{
				List<ProgramsScheduledBean> list = adminservice.getAllScheduledPrograms();
				model.addObject("schedulelist",list);
				model.setViewName("deleteScheuldes");
			}
			else{
				model.setViewName("error1");
				model.addObject("error1","SOMETHING WENT WRONG");	
			}
			
		} catch (UASException e) {
			model.setViewName("error1");
			model.addObject("error1",e.getMessage());
			
		}
		
		
		return model;
	}
	
	/*
	 * Shows the Details of the applicant
	 */
	@RequestMapping("/addApplicant")
	public ModelAndView addApplicant(@ModelAttribute("applicant") ApplicationBean applicant,BindingResult result){
		ModelAndView model = new ModelAndView();
		
			try {
				applicant=application.addApplicant(applicant);
				model.setViewName("successApplicant");// add the applicant object to the success page
			} catch (UASException e) {
				// TODO Auto-generated catch block
				model.setViewName("error");
				model.addObject("message", e.getMessage());
			}
		return model;
	}
	
	/*
	 * Status can be viewed by the applicant
	 */
	@RequestMapping("/viewstatus")
	public ModelAndView viewstatus(){
		ModelAndView model=new ModelAndView("viewStatus");
		return model;
	}
	
	/*
	 * Status can be viewed by the applicant based on applicantId
	 */
	@RequestMapping("/viewStatusById")
	public ModelAndView viewStatusById(@RequestParam("applicationId") int appId){
		
		ModelAndView model=new ModelAndView();
		ApplicationBean bean=null;
		try {
			
			bean=application.viewStatusById(appId);
			
			if(bean==null)
			{
				
				model.setViewName("error1");
				model.addObject("message","SOMETHING WENT WRONG");
			}
			else
			{
				
				model.setViewName("viewStatusById");
				model.addObject("statusBean", bean);
			}
			
		} catch (UASException e) {
			
			model.setViewName("error1");
			model.addObject("message", e.getMessage());
		}
		
		return model;
	}
	
	/*
	 * Shows the list of the applicants who scheduled their programs
	 */
	@RequestMapping("/viewAllAplicants")
	public ModelAndView applicantStatus(){
		ModelAndView model=new ModelAndView();
		List<ProgramsScheduledBean> list;
		try {
			list = macservice.viewAllScheduledPrograms();
			if(list.size()<1)
			{
				model.setViewName("error");
				model.addObject("message", "NO RECORD FOUND");
			}
			else
			{
				model.setViewName("ScheduledProgramsAfterInterview");
				model.addObject("scheduleList",list );
			}
		} catch (UASException e) {
			// TODO Auto-generated catch block
			model.setViewName("error");
			model.addObject("message", e.getMessage());
		}
	
		return model;
	}
	
	/*
	 * Shows the list of the applicant after interview
	 */
	@RequestMapping("/afterInterview")
	public ModelAndView afterInterviewStatus(@RequestParam("programId") String programId){
		ModelAndView model=new ModelAndView();
		List<ApplicationBean> list;
		try {
			list = macservice.confirmedApplicants(programId);
			if(list.size()<1)
			{
				model.setViewName("error");
				model.addObject("message", "NO RECORD FOUND");
			}
			else
			{
				model.setViewName("viewApplicantsAfterInterview");
				model.addObject("applicantList", list);
			}
		} catch (UASException e) {
			// TODO Auto-generated catch block
			model.setViewName("error");
			model.addObject("message", e.getMessage());
			
		}
		
		return model;
	}

	/*
	 * Accepts the applicant for the interview
	 */
	@RequestMapping("/accept")
	public ModelAndView confirm(@RequestParam("appId") int appId){
		ModelAndView model=new ModelAndView();
		try {
			macservice.confirm(appId);
			model.setViewName("MACHome");
		} catch (UASException e) {
			// TODO Auto-generated catch block
			model.setViewName("error");
			model.addObject("message", e.getMessage());
		}
		
		return model;
	}

	/*
	 * rejects the applicant
	 */
	@RequestMapping("/rejectAfterInterview")
	public ModelAndView rejectAfterInterview(@RequestParam("appId") int appId){
		ModelAndView model=new ModelAndView();
		try {
			macservice.reject(appId);
			model.setViewName("MACHome");
		} catch (UASException e) {
			// TODO Auto-generated catch block
			model.setViewName("error");
			model.addObject("message", e.getMessage());
		}
		
		return model;
	}
	
	/*
	 * Accepts the applicant and shows the interview date
	 */
	@RequestMapping("/confirm")
	public ModelAndView accept(@RequestParam("appId") String appId){
		ModelAndView model=new ModelAndView();
		
			
			model.setViewName("interviewDate");
			model.addObject("applicant", appId);
		
		
		return model;
	}
	
	/*
	 * Shows the date of interview for applicant
	 */
	@RequestMapping("/interview")
	public ModelAndView interviewDate(@RequestParam("appId") int appId,@RequestParam("dateOfInterview") Date date){
		ModelAndView model=new ModelAndView();
		try {
			
			macservice.accept(appId);
			macservice.interview(appId, date);
			model.setViewName("MACHome");
		} catch (UASException e) {
			// TODO Auto-generated catch block
			model.setViewName("error");
			model.addObject("message", e.getMessage());
		}
		return model;
	}
	
	/*
	 * Rejects the applicant
	 */

	@RequestMapping("/reject")
	public ModelAndView reject(@RequestParam("appId") int appId){
		ModelAndView model=new ModelAndView();
		try {
			macservice.reject(appId);
			model.setViewName("MACHome");
		} catch (UASException e) {
			// TODO Auto-generated catch block
			model.setViewName("error");
			model.addObject("message", e.getMessage());
		}
		
		return model;
	}
}
