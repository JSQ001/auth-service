package com.hand.hcf.app.prepayment.excel.service;


import java.io.IOException;

/**
 * excel 导出
 */
//@Service
public  class ExpenseExportService {


    /*@Autowired
    private ExcelExportService excelExportService;

    @Autowired
    private  CashPayRequisitionTypeService cashSobPayReqTypeService;
    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private UserService userService;

    @Autowired
    private CompanyService companyService;
    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private CashPaymentRequisitionHeadMapper headMapper;
    @Autowired
    private CashPayRequisitionTypeService typeService;

    *//**
     * 导出逻辑
     * @param exportConfig
     * @param httpServletRequest
     * @param httpServletResponse
     * @throws IOException
     *//*
    public void doExcel(ExportConfig exportConfig,
                        Wrapper<CashPaymentRequisitionHead> entityWrapper,
                        HttpServletRequest httpServletRequest,
                        HttpServletResponse httpServletResponse,
                        Map<Long, CashWriteOffDocumentAmountCO> writeOffDocumentAmountDTOMap,
                        Long companyId,
                        Long typeId,
                        Long applyId
    ) throws IOException {
        //不重复的调接口：员工
        Map<Long,String> empMap = new ConcurrentHashMap<Long,String>(16);
        //部门
        Map<Long,String> unitMap = new ConcurrentHashMap<>(16);
        //公司
        Map<Long,String> companyMap = new ConcurrentHashMap<>(16);

        //查询预付款的已付，已退金额
        List<PaymentDocumentAmountCO> payAndReturnAmount = PaymentModuleInterface.getPrepaymentPayAndReturnAmount(new ArrayList<>(), false,applyId,companyId,typeId);

        // 查询出所有的的单据类型 由于不改他原来的逻辑，所有加个查询
        List<CashPayRequisitionType> cashPayRequisitionTypes = typeService.selectList(null);

        Map<Long,String> typeMap = cashPayRequisitionTypes
                .stream()
                .collect(Collectors.toMap(CashPayRequisitionType::getId, CashPayRequisitionType::getTypeName));
        cashPayRequisitionTypes.clear();

        int total = headMapper.getTotal(entityWrapper);
        int threadNumber = total > 100000 ? 8 : 2;
        excelExportService.exportAndDownloadExcel(exportConfig, new ExcelExportHandler<CashPaymentRequisitionHead, CashPaymentRequisitionHead>() {
            @Override
            public int getTotal() {
                return total;
            }
            @Override
            public List<CashPaymentRequisitionHead> queryDataByPage(Page page) {
                // 公司
                List<CashPaymentRequisitionHead> list = headMapper.selectPage(page, entityWrapper);
                Set<Long> ids = list.stream().map(CashPaymentRequisitionHead::getCompanyId).collect(Collectors.toSet());
                Set<Long> existsCompanyIds = companyMap.keySet();
                if (!existsCompanyIds.containsAll(ids)) {
                    List<CompanySumDTO> companySumDTOS = companyService.getCompanyListByIds(new ArrayList<>(ids));
                    Map<Long, String> collect = companySumDTOS.stream().collect(Collectors.toMap(CompanySumDTO::getId, CompanySumDTO::getName, (k1, k2) -> k1));
                    companyMap.putAll(collect);
                }
                // 部门
                ids = list.stream().map(CashPaymentRequisitionHead::getUnitId).collect(Collectors.toSet());
                Set<Long> existsUnitIds = unitMap.keySet();
                if (!existsUnitIds.containsAll(ids)) {
                    List<DepartmentInfoDTO> departments = departmentService.getDepartmentByDepartmentIds(new ArrayList<>(ids));
                    Map<Long, String> collect = departments.stream().collect(Collectors.toMap(DepartmentInfoDTO::getId, DepartmentInfoDTO::getName, (k1, k2) -> k1));
                    unitMap.putAll(collect);
                }
                // 员工
                Set<Long> empIds = list.stream().map(CashPaymentRequisitionHead::getEmployeeId).collect(Collectors.toSet());
                ids = list.stream().map(CashPaymentRequisitionHead::getCreatedBy).collect(Collectors.toSet());
                ids.addAll(empIds);
                Set<Long> existsEmpIds = empMap.keySet();
                if (!existsEmpIds.containsAll(ids)) {
                    List<UserInfoDTO> users = userService.selectUsersByUserIds(new ArrayList<>(ids));
                    Map<Long, String> collect = users.stream().collect(Collectors.toMap(UserInfoDTO::getId, UserInfoDTO::getFullName, (k1, k2) -> k1));
                    empMap.putAll(collect);
                }
                return list;
            }

            @Override
            public CashPaymentRequisitionHead toDTO(CashPaymentRequisitionHead head) {
                // 创建人
                if (empMap.containsKey(head.getCreatedBy())){
                    head.setCreateByName(empMap.get(head.getCreatedBy()));
                }
                // 单据类型
                if (typeMap.containsKey(head.getPaymentReqTypeId())){
                    head.setTypeName(typeMap.get(head.getPaymentReqTypeId()));
                }
                // 员工
                if(empMap.containsKey(head.getEmployeeId())){
                    head.setEmployeeName(empMap.get(head.getEmployeeId()));
                }

                CashWriteOffDocumentAmountCO documentAmountDTO = writeOffDocumentAmountDTOMap.get(head.getId());
                if(documentAmountDTO!=null){
                    head.setNoWritedAmount(documentAmountDTO.getUnWriteOffAmount() != null ?
                            documentAmountDTO.getUnWriteOffAmount() : BigDecimal.ZERO);
                    head.setWritedAmount(documentAmountDTO.getWriteOffAmount() != null ?
                            documentAmountDTO.getWriteOffAmount() : BigDecimal.ZERO);
                }else {
                    head.setWritedAmount(BigDecimal.ZERO);
                    head.setNoWritedAmount(head.getAdvancePaymentAmount());
                }

                // 部门
                if(unitMap.containsKey(head.getUnitId())){
                    head.setUnitName(unitMap.get(head.getUnitId()));
                }
                // 公司
                if(companyMap.containsKey(head.getCompanyId())){
                    head.setCompanyName(companyMap.get(head.getCompanyId()));
                }

                if(head.getRequisitionDate()!=null){
                    head.setStringRequisitionDate(head.getRequisitionDate().toString().substring(0,10));
                }
                detailStatus(head);
                List<PaymentDocumentAmountCO> amountDTOS = payAndReturnAmount.stream().filter(
                        d -> d.getDocumentId().equals(head.getId())
                ).collect(Collectors.toList());
                if(CollectionUtils.isEmpty(amountDTOS)){
                    head.setPaidAmount(BigDecimal.ZERO);
                    head.setReturnAmount(BigDecimal.ZERO);
                }else {
                    head.setPaidAmount(amountDTOS.get(0).getPayAmount() != null ?
                            amountDTOS.get(0).getPayAmount() : BigDecimal.ZERO);
                    head.setReturnAmount(amountDTOS.get(0).getReturnAmount() != null ?
                            amountDTOS.get(0).getReturnAmount() : BigDecimal.ZERO);
                }
                return head;
            }

            @Override
            public Class<CashPaymentRequisitionHead> getEntityClass() {
                return CashPaymentRequisitionHead.class;
            }
        },threadNumber, httpServletRequest, httpServletResponse);
    }
    private void detailStatus(CashPaymentRequisitionHead headerDTO){
        switch (headerDTO.getStatus()){
            case (1001):
                headerDTO.setStatusName("编辑中");
                break;
            case (1002):
                headerDTO.setStatusName("审批中");
                break;
            case (1003):
                headerDTO.setStatusName("撤回");
                break;
            case (1004):
                headerDTO.setStatusName("审批通过");
                break;
            case (1005):
                headerDTO.setStatusName("审批驳回");
                break;
            case (2001):
                headerDTO.setStatusName("审核驳回");
                break;
            case (2002):
                headerDTO.setStatusName("审核通过");
                break;
            case (2003):
                headerDTO.setStatusName("支付中");
                break;
            case (2004):
                headerDTO.setStatusName("支付成功");
                break;
            default:
                headerDTO.setStatusName("未知");
                break;
        }
    }*/

}
