package com.hand.hcf.app.base.service;


public class WxServiceTest  {

 /*   @Autowired
    private WxService wxService;

    @MockBean
    CompanyConfigurationMapper companyConfigurationMapper;

    @Test
    public void authenticate() {
    }

    @Test
    public void buildBaseJson() {
    }

    @Test
    public void getCompanyConfiguration() {
    }

    @Test
    public void httpsPostJson() {
    }

    @Test
    public void loadUserByUsername() {
        given(companyConfigurationMapper.selectByMap(Mockito.anyMap())).willReturn(getaCompanyConfiguration());

        assertThatThrownBy(() -> wxService.loadUserByUsername("yxsF2vHmIzEBKF0h7X7U8PSEx4ay7hrBICDmVMwWvZY", "ww284c3d009e081043", "ww7004f021bfb6099a", ""))
                .isInstanceOf(UserNotActivatedException.class)
                .hasMessage("user.not.found");
    }

    private List<CompanyConfiguration> getaCompanyConfiguration() {
        List<CompanyConfiguration> rs = new ArrayList<>();
        CompanyConfiguration c = new CompanyConfiguration();
        ConfigurationDetail configuration = new ConfigurationDetail();
        ConfigurationDetail.WxConfiguration wxConfiguration = new ConfigurationDetail.WxConfiguration();
        wxConfiguration.setCorpId("a");
        wxConfiguration.setSecretKey("b");
        configuration.setWxConfiguration(wxConfiguration);
        c.setConfiguration(configuration);
        rs.add(c);
        return rs;
    }*/
}