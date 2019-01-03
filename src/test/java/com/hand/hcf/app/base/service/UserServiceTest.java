package com.hand.hcf.app.base.service;


public class UserServiceTest  {

   /* @Autowired
    private UserService userService;

    @MockBean
    CompanySecurityMapper companySecurityMapper;

    @Test
    public void findOneByContactEmail() {
        String s = "yunong.li@hand-china.com";
        UserDTO userDTO = userService.findOneByContactEmail(s);
        assertThat(userDTO.getEmail()).isEqualTo(s);
    }

    @Test
    public void loginCommonCheck() {
        given(companySecurityMapper.selectByMap(Mockito.anyMap())).willReturn(getCompanySecurity());
        String s = "yunong.li@hand-china.com";
        UserDTO userDTO = userService.findOneByContactEmail(s);
        userService.loginCommonCheck(userDTO);
    }

    private List<CompanySecurity> getCompanySecurity() {
        List<CompanySecurity> rs = new ArrayList<>();
        CompanySecurity c = new CompanySecurity();
        c.setPasswordExpireDays(1);
        rs.add(c);
        return rs;
    }*/
}