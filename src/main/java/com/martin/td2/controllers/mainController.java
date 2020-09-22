package com.martin.td2.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

import com.martin.td2.models.Organization;
import com.martin.td2.models.User;
import com.martin.td2.repositories.OrgaRepository;
import com.martin.td2.repositories.UserRepository;

@Controller
@RequestMapping("/orga/")
public class mainController {
	@Autowired
	private OrgaRepository orgaRepo;
	
	@Autowired
	private UserRepository userRepo;
	
	@GetMapping(value={"","index"})
    public String index(ModelMap model) {
		List<Organization> organizations = orgaRepo.findAll();
		model.put("organizations", organizations);
		return "index";
	}
	
	@GetMapping("new")
	public String orgaNew() {
		return ("newOrga");
	}
	
	@PostMapping("addNew/")
	public RedirectView addOrga(@RequestParam String name,@RequestParam String domain,@RequestParam String aliases,@RequestParam String organizationsettings) {
		Organization orga = new Organization(name,domain,aliases,organizationsettings);
		orgaRepo.saveAndFlush(orga);
		return new RedirectView("/orga/");
	}
	
	@GetMapping("findbyid/{id}")
	public @ResponseBody String getOrga(@PathVariable int id) {
		Organization organizations = orgaRepo.findById(id);
		if(organizations != null) {
			return organizations.toString();
		}
		return "Organisation non trouvée";
	}
	@GetMapping("newuser/{userName}/{orgaName}")
	public @ResponseBody String addUserInOrga(@PathVariable String userName,@PathVariable String orgaName) {
		Optional<Organization> optional = orgaRepo.findByName(orgaName);
		if(optional.isPresent()) {
			User user = new User();
			user.setFirstName(userName);
			user.setOrganization(optional.get());
			userRepo.saveAndFlush(user);
			return user + " ajoutée";
		}
		return "Organisation " + orgaName + " inexistante !";
	}
	
	@GetMapping("display/{id}")
    public String display(ModelMap model,@PathVariable int id) {
		Organization organization = orgaRepo.findById(id);
		model.put("organization", organization);
		return "displayOrga";
	}
	
	@GetMapping("edit/{id}")
	public String edit(ModelMap model,@PathVariable int id) {
		model.put("id", id);
		return "editOrga";
	}
	
	@PostMapping("editOrga/")
	public RedirectView editOrga(@RequestParam int id,@RequestParam String name,@RequestParam String domain,@RequestParam String aliases) {
		Organization organization = orgaRepo.findById(id);
		if((organization != null)) {
			organization.setName(name);
			organization.setDomain(domain);
			organization.setAliases(aliases);
			orgaRepo.saveAndFlush(organization);
		}
		return new RedirectView("../");
	}
	
	@GetMapping("delete/{id}")
	public RedirectView delete(@PathVariable int id) {
		Organization organization = orgaRepo.findById(id);
		if((organization != null)) {
			orgaRepo.deleteById(id);
		}
		return new RedirectView("../");
	}
}