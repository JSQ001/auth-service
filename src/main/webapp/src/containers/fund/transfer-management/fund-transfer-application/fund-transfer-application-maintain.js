import React from 'react';
import {
  Form,
  Table,
  Button,
  message,
  Input,
  Alert,
  Modal,
  Row,
  Col,
  DatePicker,
  InputNumber,
  Icon,
} from 'antd';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import Lov from 'widget/Template/lov';
import moment from 'moment';
import Upload from 'widget/upload-button';
import Chooser from 'widget/chooser';
import config from 'config';
import PaymentMaintenanceService from './fund-transfer-application.service';

const { confirm } = Modal;
const FormItem = Form.Item;

class ManualMaintain extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      // uploadOIDs: [], // 附件oid
      dataTemp: [], // 新增时用来push到tableData中
      tableData: [], // 数据列表
      isNew: true,
      isUpload: false, // 附件上传
      // visible: false, // 选择银行账号
      // paymentPurpose: [], // 付款用途值列表
      // paymentBaseInfo: {}, // 保存的头数据
      applicationInformation: {}, // 根据id查询得到的数据
      isTrue: false,
      pagination: {
        total: 0, // 数据总数
        page: 0, // 用于计算页数
        pageSize: 10, // 每页条数
        current: 1, // 当前页数
        showSizeChanger: true, // 是否可以改变 pageSize
        showQuickJumper: true, // 是否可以快速跳转至某页
        showTotal: (total, range) => `显示${range[0]}-${range[1]} 共 ${total} 条`, // 用于显示数据总量和当前数据顺序
      },
      columns: [
        {
          title: '序号',
          dataIndex: 'tradeCode',
          width: 50,
          align: 'center',
        },
        {
          title: '下拨日期',
          dataIndex: 'downDate',
          width: '15%',
          align: 'center',
          render: (value, record, index) => {
            const {
              form: { getFieldDecorator },
            } = this.props;
            const {
              status,
              // isNew,
            } = this.state;
            return status !== 'BATCH_PAYMENT' ? (
              <Form>
                <FormItem>
                  {getFieldDecorator(`c${index.toString()}`, {
                    initialValue: record.downDate ? moment(record.downDate) : '',
                  })(
                    <DatePicker
                      format="YYYY-MM-DD"
                      onChange={e => this.onDescChange(e, index, 'downDate')}
                    />
                  )}
                </FormItem>
              </Form>
            ) : (
              <span>{record.outCompanyName}</span>
            );
          },
        },
        {
          title: '调出公司',
          dataIndex: 'outCompanyName',
          width: '10%',
          align: 'center',
          render: (value, record, index) => {
            const {
              form: { getFieldDecorator },
            } = this.props;
            const {
              status,
              // isNew,
            } = this.state;
            return status !== 'BATCH_PAYMENT' ? (
              <Form>
                <FormItem>
                  {getFieldDecorator(`a${index.toString()}`, {
                    // initialValue: isNew ? [{ id: record.inCompanyId, name: record.inCompanyName }] : '',
                    initialValue: record.inCompanyId
                      ? [{ id: record.inCompanyId, name: record.inCompanyName }]
                      : '',
                  })(
                    <Chooser
                      type="company"
                      allowClear
                      labelKey="name"
                      valueKey="id"
                      single
                      showClear={false}
                      onChange={e => this.onDescChange(e, index, 'outCompanyName')}
                      listExtraParams={{ setOfBooksId: props.company.setOfBooksId }}
                    />
                  )}
                </FormItem>
              </Form>
            ) : (
              <span>{record.outCompanyName}</span>
            );
          },
        },
        {
          title: '调入公司',
          dataIndex: 'inCompanyName',
          width: '10%',
          align: 'center',
          render: (value, record, index) => {
            const {
              form: { getFieldDecorator },
            } = this.props;
            const {
              status,
              // isNew,
            } = this.state;
            return status !== 'BATCH_PAYMENT' ? (
              <Form>
                <FormItem>
                  {getFieldDecorator(index.toString(), {
                    // initialValue: isNew ? [{ id: record.inCompanyId, name: record.inCompanyName }] : '',
                    initialValue: record.inCompanyId
                      ? [{ id: record.inCompanyId, name: record.inCompanyName }]
                      : '',
                  })(
                    <Chooser
                      type="company"
                      allowClear
                      labelKey="name"
                      valueKey="id"
                      single
                      showClear={false}
                      onChange={e => this.onDescChange(e, index, 'inCompanyName')}
                      listExtraParams={{ setOfBooksId: props.company.setOfBooksId }}
                    />
                  )}
                </FormItem>
              </Form>
            ) : (
              <span>{record.inCompanyName}</span>
            );
          },
        },
        {
          title: '调入账号',
          dataIndex: 'inAccount',
          width: '10%',
          align: 'center',
          render: (value, record, index) => {
            const {
              form: { getFieldDecorator },
            } = this.props;
            const {
              status,
              // isNew,
            } = this.state;
            return status !== 'BATCH_PAYMENT' ? (
              <Form>
                <FormItem>
                  {getFieldDecorator(`b${index.toString()}`, {
                    // initialValue: !isNew ? { id: record.id, accountNumber: record.inAccount } :'',
                    initialValue: { id: record.id, accountNumber: record.inAccount },
                  })(
                    <Lov
                      code="bankaccount_choose"
                      valueKey="accountNumber"
                      labelKey="id"
                      single
                      onChange={e => this.onDescChange(e, index, 'inAccount')}
                    />
                  )}
                </FormItem>
              </Form>
            ) : (
              <span>{record.inAccount}</span>
            );
          },
        },
        {
          title: '金额',
          dataIndex: 'inAmount',
          width: '10%',
          align: 'center',
          render: (value, record, index) => {
            const { status } = this.state;
            return status !== 'BATCH_PAYMENT' ? (
              <InputNumber
                placeholder="请输入"
                allowClear
                defaultValue={record.amount ? record.amount : value}
                /* eslint-disable */
                onChange={value => this.handleValueChange(value, index, 'inAmount')}
                /* eslint-enable */
                style={{ textAlign: 'center' }}
              />
            ) : (
              <span>{record.amount}</span>
            );
          },
        },
        {
          title: '币种',
          dataIndex: 'currency',
          width: '10%',
          align: 'center',
          render: (value, record) => {
            const { currency } = this.state;
            return <span>{record.currency || currency}</span>;
          },
        },
        {
          title: '银行摘要',
          dataIndex: 'purpose',
          width: '10%',
          align: 'center',
          render: (value, record, index) => {
            const { status } = this.state;
            return status !== 'BATCH_PAYMENT' ? (
              <Input
                placeholder="请输入"
                // allowClear
                defaultValue={record.description ? record.description : value}
                min={10}
                onChange={e => this.onDescChange(e, index, 'description')}
                style={{ textAlign: 'center' }}
              />
            ) : (
              <span>{record.description}</span>
            );
          },
        },
        {
          title: '资金用途',
          dataIndex: 'description',
          width: '10%',
          align: 'center',
          render: (value, record, index) => {
            const { status } = this.state;
            return status !== 'BATCH_PAYMENT' ? (
              <Input
                // allowClear
                placeholder="请输入"
                defaultValue={record.description ? record.description : value}
                min={10}
                onChange={e => this.onDescChange(e, index, 'description')}
                style={{ textAlign: 'center' }}
              />
            ) : (
              <span>{record.description}</span>
            );
          },
        },
        {
          title: '附件',
          dataIndex: 'attachment',
          width: 50,
          align: 'center',
          // render: (value, record, index) => {
          //   const { status } = this.state;
          //   return (
          //     <a
          //       onClick={event => {
          //       event.stopPropagation();
          //       this.upload();
          //     }}
          //     >
          //       {' '}
          //     附件
          //     </a>
          //   );
          // },
        },
      ],
      // columns1: [
      //   {
      //     title: '银行账号',
      //     dataIndex: 'accountNumber',
      //     width: '15%',
      //     align: 'center',
      //   },
      //   {
      //     title: '账户名称',
      //     dataIndex: 'accountName',
      //     width: '15%',
      //     align: 'center',
      //   },
      //   {
      //     title: '币种',
      //     dataIndex: 'currencyCode',
      //     width: '15%',
      //     align: 'center',
      //   },
      // ],
    };
  }

  componentDidMount() {
    const { match } = this.props;
    if (match.params.id === 'new') {
      // console.log('new');
      this.setState({
        styleNone: {
          marginLeft: '15px',
        },
        styleShow: {
          marginLeft: '15px',
          display: 'none',
        },
      });
    } else {
      this.setState({
        isNew: false,
        status: match.params.status, // 付款单类型
        userId: match.params.id,
      });
      this.getModifyAccountDetail(match.params.id);
    }
  }

  /**
   * 根据头ID获取详情
   */
  getModifyAccountDetail = id => {
    PaymentMaintenanceService.getModifyAccountDetail(id).then(res => {
      if (res.data.headerDTO.billStatus === 'ZJ_APPROVED') {
        this.setState({
          styleNone: {
            marginLeft: '15px',
            display: 'none',
          },
          styleShow: {
            marginLeft: '15px',
            display: 'block',
          },
        });
      }
      this.setState({
        applicationInformation: res.data.headerDTO,
        tableData: res.data.listResponseEntity.body,
      });
    });
  };

  /**
   * 数据分页点击
   */
  onChangePager = pagination => {
    const temp = {};
    temp.page = pagination.current - 1;
    temp.current = pagination.current;
    temp.pageSize = pagination.pageSize;
    this.setState(
      {
        pagination: temp,
      },
      () => {
        this.getList();
      }
    );
  };

  /**
   * 银行账户的搜索
   */
  search = e => {
    const { form } = this.props;
    e.preventDefault();
    form.validateFields((err, values) => {
      if (values.account) {
        this.getBackList(values.account);
      } else {
        this.getBackList();
      }
    });
  };

  /**
   * 搜索条件重置
   */
  searchClear = e => {
    const { form } = this.props;
    e.preventDefault();
    form.resetFields();
    this.getBackList();
  };

  /**
   * 新建
   */
  handleListShow = () => {
    const { tableData, dataTemp } = this.state;
    const value = {
      idSun: 1, // 删除未保存的行用到的字段
      cardSign: 'BANK_CARD',
      // sequence: 10,
      status: 'new', // 判断行是否新建
    };
    // dataTemp[tableData.length] = value;
    tableData.push(value);
    this.setState({ tableData, dataTemp });
  };

  /**
   * 提示框显示
   */
  noticeAlert = rows => {
    const noticeAlert = (
      <span>
        已选择<span style={{ fontWeight: 'bold', color: '#108EE9' }}> {rows.length} </span> 项
      </span>
    );
    this.setState({
      noticeAlert: rows.length ? noticeAlert : null,
      // batchDelete: !rows.length,
    });
  };

  /**
   * 行选择
   */
  onSelectChange = (selectedRowKeys, selectedRow) => {
    this.setState(
      {
        selectedRowKeys,
        selectedRow,
        // batchDelete: !(selectedRowKeys.length > 0),
      },
      () => {
        if (selectedRowKeys.length > 0) {
          this.noticeAlert(selectedRow);
        } else {
          this.setState({
            noticeAlert: null,
          });
        }
      }
    );
  };

  /**
   * 附件上传
   */
  upload = () => {
    this.setState({
      isUpload: true,
    });
  };

  /**
   * 附件上传关闭关闭
   */
  onClose = () => {
    this.setState({
      isUpload: false,
    });
  };

  // /**
  //  * 上传附件
  //  */
  // handleUpload = OIDs => {
  //   let { uploadOIDs } = this.state;
  //   console.log(uploadOIDs);
  //   OIDs.forEach(item => {
  //     uploadOIDs.push(item);
  //   });
  //   console.log(uploadOIDs);
  //   this.setState({
  //     uploadOIDs: uploadOIDs,
  //   });
  // };

  /**
   * 保存
   */
  handleSaveClick = e => {
    e.preventDefault();
    const {
      form: { getFieldsValue },
      user,
    } = this.props;
    const { tableData, isNew } = this.state;
    // const {isNew} = this.props
    // if(idNew || userId){
    const { match } = this.props;
    let saveDateHead = {}; // 保存的头信息
    let saveDate = {}; // 总的保存信息
    let amountNum = 0; // 总金额
    if (tableData.length > 0) {
      tableData.forEach(val => {
        amountNum += val.inAmount;
      });
    }
    const params = getFieldsValue();
    saveDateHead = {
      id: isNew ? '' : match.params.id,
      companyId: params.paymentCompanyName ? params.paymentCompanyName[0].id : '', // 申请公司
      departmentId: params.department ? params.department[0].departmentId : '', // 申请部门
      billDate: params.billDate // 申请日期
        ? moment(params.billDate)
        : '',
      description: params.description || '', // 描述
      employeeId: params.employeeId ? user.id : '', // 员工
      lineCount: tableData.length,
      amount: amountNum || '0', // 总金额
    };
    // 总的保存信息
    saveDate = {
      header: saveDateHead, // 头数据
      lineList: tableData, // 行数据
    };
    let noSave = false;
    // if (billType !== 'BATCH_PAYMENT') {
    /* eslint-disable */
    if (tableData.length > 0) {
      for (let i = 1; i <= tableData.length; i += 1) {
        console.log('---', Object.keys(tableData[i - 1]).length);
        let a = Object.keys(tableData[i - 1]).length;
        /* 必输判断 */
        if (a < 1) {
          noSave = true;
          break;
        } else if (
          tableData[i - 1].amount === null ||
          tableData[i - 1].outCompanyId === '' ||
          tableData[i - 1].gatherAccount === '' ||
          tableData[i - 1].description === '' ||
          tableData[i - 1].gatherBranchBankName === ''
        ) {
          noSave = true;
          break;
        }
      }
    }

    // }
    if (
      saveDateHead.companyId === '' ||
      saveDateHead.departmentId === '' ||
      saveDateHead.billDate === ''
    ) {
      message.error('请填写完整的头信息');
    } else if (noSave) {
      message.error('必输字段未不能为空');
    } else {
      console.log(saveDate);
      /* eslint-enable */
      // PaymentMaintenanceService.createOrUpdate(saveDate).then(res => {
      //   message.success('保存成功');
      //   this.getModifyAccountDetail(match.params.id)
      // if (match.params.id) {
      //   this.getList(match.params.id);
      //   this.getUserList(match.params.paymentBatchNumber);
      // } else {
      //   this.setState({
      //     idNew: res.data.paymentBaseInfo.id,
      //     paymentBatchNumberNew: res.data.paymentBaseInfo.paymentBatchNumber,
      //   });
      //   this.getUserList(res.data.paymentBaseInfo.paymentBatchNumber);
      //   this.getList(res.data.paymentBaseInfo.id);
      // }
      // });
      // .catch(err => {
      //   message.error(err.response.data.message);
      // });
    }
    // }
  };

  /**
   * 展示删除弹框
   */
  showDeleteConfirm = () => {
    const aThis = this;
    if (aThis.state.selectedRowKeys) {
      confirm({
        title: '确认删除?',
        okText: '确定',
        okType: 'danger',
        cancelText: '取消',
        onOk() {
          aThis.deleteItems();
        },
      });
    }
  };

  /**
   * 删除行
   */
  deleteItems = async () => {
    // 从数据库删除的数据ID集合
    const hasIdselected = [];
    // 从数据库删除成功的标志
    let deleteDateSuccessFlag = true;
    // 删除返回的result
    let result = '';
    const { selectedRow, tableData } = this.state;
    // 收集有ID的数据集合
    selectedRow.forEach(item => {
      if (item.id) {
        hasIdselected.push(item.id);
      }
    });
    // 选出删除的后的table
    const resTableData = tableData.filter(item => {
      return !selectedRow.includes(item);
    });
    // 如果选中数据有ID就执行后端删除操作
    if (hasIdselected.length > 0) {
      result = await PaymentMaintenanceService.deleteManualList(hasIdselected);
    }
    if (result) {
      if (result.status === 200) {
        deleteDateSuccessFlag = true;
      } else {
        deleteDateSuccessFlag = false;
      }
    }
    if (deleteDateSuccessFlag) {
      message.success('删除成功！');

      this.setState({
        tableData: resTableData,
        selectedRow: [],
        selectedRowKeys: [],
        noticeAlert: null,
      });
    } else {
      message.error(result.response.data.message);
    }
    // this.handleSaveClickDelete();
  };

  /**
   * 删除之后的保存
   */
  handleSaveClickDelete = () => {
    const { tableData, isNew } = this.state;
    const { match } = this.props;
    let saveDateHead = {}; // 保存的头信息
    let saveDate = {}; // 总的保存信息
    saveDateHead = {
      id: isNew ? '' : match.params.id,
    };
    // 总的保存信息
    saveDate = {
      paymentBaseInfo: saveDateHead, // 头数据
      lineList: tableData, // 行数据
    };
    PaymentMaintenanceService.createOrUpdate(saveDate).then(res => {
      if (isNew) {
        this.getList(res);
      } else if (match.params.id) {
        this.getModifyAccountDetail(match.params.id);
      }
    });
  };

  /**
   * 返回
   */
  back = () => {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/transfer-managementfund-transfer-application/fund-transfer-application`,
      })
    );
  };

  /**
   * 监听form控件值的改变(InputNumber)
   * */
  handleValueChange = (value, index, dataIndex) => {
    const { tableData } = this.state;
    // console.log(value)
    if (dataIndex === 'inAmount') {
      tableData[index].inAmount = value; // 金额
    }
  };

  /**
   * 输入框变化事件
   */
  onDescChange = (e, index, dataIndex) => {
    const { tableData } = this.state;
    // console.log(e)
    if (dataIndex === 'outCompanyName') {
      tableData[index].outCompanyId = e[0].id; // 调出公司
    } else if (dataIndex === 'inCompanyName') {
      tableData[index].inCompanyId = e[0].id; // 调入公司
    } else if (dataIndex === 'inAccount') {
      tableData[index].inAccountId = e.id;
      tableData[index].currency = e.currencyCode; // 调入账号
      this.setState({
        currency: e.currencyCode,
      });
    } else if (dataIndex === 'description') {
      tableData[index].description = e.target.value; // 摘要
    } else if (dataIndex === 'purpose') {
      tableData[index].purpose = e.target.value; // 摘要
    } else if (dataIndex === 'downDate') {
      tableData[index].downDate = moment(e); // 下拨日期
    }
  };

  /**
   * 提交
   */
  handleCreateClick = () => {
    const { idNew, userId, allAccount, money } = this.state;
    if (idNew || userId) {
      const { dispatch } = this.props;
      if (allAccount || money) {
        PaymentMaintenanceService.submit(idNew || userId)
          .then(() => {
            message.success('提交成功');
            dispatch(
              routerRedux.push({
                pathname: `/payment-management/payment-slip-maintenance/payment-slip-maintenance`,
              })
            );
          })
          .catch(err => {
            message.error(err.response.data.message);
          });
      } else {
        message.error('金额不能为0');
      }
    } else {
      message.error('请先保存数据');
    }
  };

  render() {
    const {
      form: { getFieldDecorator },
      company,
      user,
    } = this.props;
    const {
      paymentBatchNumber,
      description,
      billDate,
      pagination,
      columns,
      noticeAlert,
      loading,
      selectedRow,
      selectedRowKeys,
      tableData,
      isTrue,
      paymentBatchNumberNew,
      isNew,
      applicationInformation,
      styleNone,
      styleShow,
      isUpload,
    } = this.state;
    const rowSelection = {
      selectedRowKeys,
      selectedRow,
      onChange: this.onSelectChange,
    };

    return (
      <div className="train">
        <Modal title="附件上传" visible={isUpload} onOk={this.onClose}>
          <Upload
            attachmentType="CONTRACT"
            uploadUrl={`${config.baseUrl}/api/upload/static/attachment`}
            fileNum={9}
            uploadHandle={this.handleUpload}
            // defaultFileList={fileList}
            // defaultOIDs={isNew ? [] : model.attachmentOidList}
          />
        </Modal>
        <div className="table-header">
          <Form style={{ marginTop: '-10px' }}>
            <Row>
              <Col span={5} offset={1}>
                <FormItem label="单据编号:" style={{ marginBottom: '0px' }}>
                  {getFieldDecorator('applyNumber', {
                    initialValue: isNew
                      ? paymentBatchNumberNew
                      : applicationInformation.applyNumber,
                  })(<Input disabled placeholder={paymentBatchNumber} />)}
                </FormItem>
              </Col>

              <Col span={5} offset={1}>
                <FormItem label="申请公司" style={{ marginBottom: '0px' }}>
                  {getFieldDecorator('paymentCompanyName', {
                    initialValue: isNew
                      ? [{ id: company.id, name: company.name }]
                      : [
                          {
                            id: applicationInformation.companyId,
                            name: applicationInformation.companyName,
                          },
                        ],
                  })(
                    <Chooser
                      type="company"
                      labelKey="name"
                      valueKey="id"
                      single
                      showClear={false}
                      listExtraParams={{ setOfBooksId: company.setOfBooksId }}
                    />
                  )}
                </FormItem>
              </Col>
              <Col span={5} offset={1}>
                <FormItem label="申请部门">
                  {getFieldDecorator('department', {
                    rules: [{ required: true, message: this.$t('common.please.select') }],
                    initialValue: isNew
                      ? [{ departmentId: user.departmentId, path: user.departmentName }]
                      : [
                          {
                            departmentId: applicationInformation.departmentId,
                            path: applicationInformation.departmentName,
                          },
                        ],
                  })(
                    <Chooser
                      type="department_document"
                      labelKey="path"
                      valueKey="departmentId"
                      single
                      showClear={false}
                      listExtraParams={{ tenantId: user.tenantId }}
                      disabled={isTrue}
                    />
                  )}
                </FormItem>
              </Col>
              <Col span={5} offset={1}>
                <FormItem label="申请人" style={{ marginBottom: '0px' }}>
                  {getFieldDecorator('employeeId', {
                    initialValue: isNew ? user.userName : applicationInformation.employeeName,
                  })(<Input disabled />)}
                </FormItem>
              </Col>
            </Row>
            <Row>
              <Col span={5} offset={1}>
                <FormItem label="申请日期" style={{ marginBottom: '0px' }}>
                  {getFieldDecorator('billDate', {
                    initialValue: isNew
                      ? moment(billDate) || ''
                      : moment(applicationInformation.billDate) || '',
                  })(<DatePicker format="YYYY-MM-DD" />)}
                </FormItem>
              </Col>
              <Col span={8} offset={1}>
                <FormItem label="说明" style={{ marginBottom: '0px' }}>
                  {getFieldDecorator('description', {
                    initialValue: isNew
                      ? description || ''
                      : applicationInformation.description || '',
                  })(<Input placeholder="请输入" />)}
                </FormItem>
              </Col>
            </Row>
          </Form>
          {/* 数据列表 */}
          <div className="table-header-buttons">
            <Row style={{ marginTop: '20px' }}>
              <Col>
                {/* 新建 */}
                <Button type="primary" onClick={() => this.handleListShow(true)} style={styleNone}>
                  {this.$t('新增行')}
                </Button>
                {/* 删除 */}
                <Button
                  type="danger"
                  style={styleNone}
                  onClick={e => {
                    e.preventDefault();
                    e.stopPropagation();
                    this.showDeleteConfirm();
                  }}
                >
                  {this.$t('删除行')}
                </Button>
                <Button type="primary" style={styleNone} onClick={this.handleSaveClick}>
                  {this.$t('保存')}
                </Button>
                <Button type="primary" onClick={this.handleCreateClick} style={styleNone}>
                  {this.$t('提交')}
                </Button>
                <Button type="primary" style={styleShow}>
                  {this.$t('关闭')}
                </Button>
              </Col>
            </Row>
          </div>
          {/* 提示信息 */}
          {noticeAlert ? (
            <Alert message={noticeAlert} type="info" showIcon style={{ marginBottom: '10px' }} />
          ) : (
            ''
          )}
          <Table
            rowKey={record => record.id}
            columns={columns}
            dataSource={tableData}
            pagination={pagination}
            rowSelection={rowSelection}
            loading={loading}
            onChange={this.onChangePager}
            // onRowClick={this.handleRowClick}
            bordered
            size="middle"
            scroll={{ x: 1500 }}
          />
          <a style={{ fontSize: '14px', paddingBottom: '40px' }} onClick={this.back}>
            <Icon type="rollback" style={{ marginRight: '5px', paddingBottom: '15px  ' }} />返回
          </a>
        </div>
      </div>
    );
  }
}
/**
 * 建立组件和数据的映射关系 注意state必传 返回的是需要绑定的model
 * @param {*} state
 */
function map(state) {
  return {
    user: state.user.currentUser,
    company: state.user.company,
  };
}
/**
 * 关联 model
 */
export default connect(map)(Form.create()(ManualMaintain));
