import React, { Component } from 'react';
import { Form, Input, Row, Col, Button, Select, message, Spin, Modal } from 'antd';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import Chooser from 'widget/chooser';
import Upload from 'widget/upload';
import ContractDetail from 'containers/contract/contract-approve/contract-detail-common';
import Lov from 'widget/Template/lov';

import service from './service';
import config from 'config';

const FormItem = Form.Item;

import moment from 'moment';

class NewExpenseApplicationFrom extends Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
      pageLoading: true,
      isNew: true,
      model: {},
      currencyList: [],
      dimensionList: [],
      applicationTypeInfo: {},
      typeId: '',
      uploadOIDs: [],
      contractParams: {
        companyId: props.user.companyId,
        currency: props.company.baseCurrency,
        documentType: 'PREPAYMENT_REQUISITION',
      },
      dimensionValues: {},
      defaultUser: {},
      contractId: '',
      select_contract: {
        title: this.$t('expense.select.the.contract') /*选择合同*/,
        url: `${config.contractUrl}/api/contract/document/relations/associate/header/query`,
        searchForm: [
          {
            type: 'input',
            label: this.$t('expense.contract.number') /*合同编号*/,
            id: 'contractNumber',
          },
        ],
        columns: [
          { title: this.$t('expense.contract.number'), dataIndex: 'contractNumber' } /*合同编号*/,
          {
            title: this.$t('expense.the.contract.type'),
            dataIndex: 'contractTypeName',
          } /*合同类型*/,
          {
            title: this.$t('expense.name.of.the.contract'),
            dataIndex: 'contractName',
          } /*合同名称*/,
          {
            title: this.$t('expense.operation') /*操作*/,
            dataIndex: 'contractHeaderId',
            render: value => (
              <a
                onClick={e => {
                  this.contractDetail(value, e);
                }}
              >
                {this.$t('expense.wallet.checkDetail')}
                {/*查看详情*/}
              </a>
            ),
          },
        ],
        key: 'contractHeaderId',
      },
    };
  }

  componentDidMount() {
    if (this.props.match.params.id) {
      service
        .getEditInfo(this.props.match.params.id)
        .then(res => {
          let fileList = res.data.attachments
            ? res.data.attachments.map(o => ({
                ...o,
                uid: o.attachmentOid,
                name: o.fileName,
                status: 'done',
              }))
            : [];

          this.setState({
            isNew: false,
            model: res.data,
            uploadOIDs: fileList.map(o => o.uid),
            fileList,
            pageLoading: false,
            contractParams: {
              companyId: res.data.companyId,
              currency: res.data.currencyCode,
              documentType: 'PREPAYMENT_REQUISITION',
            },
            dimensionList: res.data.dimensions || [],
          });
        })
        .catch(err => {
          message.error(err.response.data.message);
        });
    }

    // 获取币种列表
    service.getCurrencyList(this.props.company.companyOid).then(res => {
      this.setState({ currencyList: res.data });
    });

    if (this.props.match.params.typeId) {
      this.getApplicationTypeInfo(this.props.match.params.typeId);
      this.listUserByTypeId(this.props.match.params.typeId);
    }
  }

  // 查看合同详情
  contractDetail = (contractId, e) => {
    e.stopPropagation();
    this.setState({ contractId, contractShow: true });
  };

  getApplicationTypeInfo = typeId => {
    const { contractParams } = this.state;
    service
      .getHeaderInfoByNew(typeId)
      .then(res => {
        let dimensionList = res.data.dimensions || [];
        this.setState({
          applicationTypeInfo: res.data,
          pageLoading: false,
          dimensionList,
        });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
    this.setState({ typeId });
  };

  setDimension = (dimensions, companyId, unitId, userId) => {
    if (dimensions !== null && dimensions.length !== 0) {
      let dimensionIds = dimensions.map(item => item.dimensionId);
      service
        .getDimensionItemsByIds(dimensionIds, companyId, unitId, userId)
        .then(res => {
          let temp = res.data;
          dimensions.forEach(e => {
            let items = temp.find(o => o.id === e.dimensionId)['subDimensionItemCOS'];
            e.options = items;
            this.props.form.setFieldsValue({ ['dimension-' + e.dimensionId]: undefined });
          });
          this.setState({
            dimensionList: dimensions || [],
          });
        })
        .catch(err => {
          message.error(err.response.data.message);
        });
    }
  };

  listUserByTypeId = typeId => {
    service
      .listUserByTypeId(typeId)
      .then(res => {
        if (res.data) {
          let { defaultUser } = this.state;
          const currentUser = res.data.find(o => o.id === this.props.user.id);
          if (currentUser) {
            defaultUser = currentUser;
          } else {
            defaultUser = res.data[0];
          }
          this.setState({ defaultUser });
          this.userInit(defaultUser);
        }
      })
      .catch(err => {
        message.error('请求失败，请稍后重试...');
      });
  };

  //返回
  onBack = () => {
    if (!this.state.isNew) {
      this.props.dispatch(
        routerRedux.push({
          pathname:
            '/expense-application/expense-application/expense-application-detail/' +
            this.props.match.params.id,
        })
      );
    } else {
      this.props.dispatch(
        routerRedux.push({
          pathname: '/expense-application/expense-application/expense-application-form',
        })
      );
    }
  };

  //上传附件
  handleUpload = OIDs => {
    this.setState({
      uploadOIDs: OIDs,
    });
  };

  //表单提交
  handleSave = e => {
    e.preventDefault();
    this.props.form.validateFields((err, values) => {
      if (err) return;
      this.setState({ loading: true });
      let { typeId, uploadOIDs, isNew, model, dimensionList } = this.state;

      Object.keys(values).map(key => {
        if (key.indexOf('-') >= 0) {
          let dimensionId = key.split('-')[1];
          let record = dimensionList.find(o => o.dimensionId == dimensionId);
          record.value = values[key];
        }
      });

      dimensionList.map(o => {
        if (!o.value) {
          o.value = o.defaultValue;
        }
      });
      if (isNew) {
        values = {
          typeId,
          employeeId: values.user.id,
          remarks: values.remarks,
          currencyCode: values.currencyCode,
          companyId: values.company[0].id,
          departmentId: values.department[0].departmentId,
          dimensions: dimensionList,
          attachmentOid: uploadOIDs.length ? uploadOIDs.join(',') : null,
          requisitionDate: moment().format(),
          contractHeaderId:
            values.contract && values.contract.length ? values.contract[0].contractHeaderId : '',
        };
      } else {
        values = {
          ...model,
          id: model.id,
          typeId: model.typeId,
          employeeId: values.user.id,
          remarks: values.remarks,
          currencyCode: values.currencyCode,
          companyId: values.company[0].id,
          departmentId: values.department[0].departmentId,
          dimensions: isNew ? dimensionList : dimensionList.filter(o => o.headerFlag),
          attachmentOid: uploadOIDs.length ? uploadOIDs.join(',') : null,
          requisitionDate: model.requisitionDate,
          versionNumber: model.versionNumber,
          contractHeaderId:
            values.contract && values.contract.length ? values.contract[0].contractHeaderId : '',
        };
      }
      let method = service.addExpenseApplictionForm;
      if (!isNew) {
        method = service.updateHeaderData;
      }
      method(values)
        .then(res => {
          message.success(this.$t('expense.operation.is.successful1')); /*操作成功！*/
          this.setState({ loading: false });
          this.props.dispatch(
            routerRedux.push({
              pathname:
                '/expense-application/expense-application/expense-application-detail/' +
                res.data.id,
            })
          );
        })
        .catch(err => {
          message.error(err.response.data.message);
          this.setState({ loading: false });
        });
    });
  };

  currencyChange = value => {
    this.props.form.setFieldsValue({ contract: [] });
    this.setState({ contractParams: { ...this.state.contractParams, currency: value } });
  };

  companyChange = value => {
    this.props.form.setFieldsValue({ contract: [] });
    this.setState({ contractParams: { ...this.state.contractParams, companyId: value[0].id } });
    this.userOrCompanyOrUnitChange(value[0].id, 'oldId', 'oldId');
  };

  userInit = user => {
    service
      .getUserInfoByTypeId(user.userOid)
      .then(res => {
        let temp = res.data;
        let company = [{ id: temp.companyId, name: temp.companyName }];
        let department = [{ departmentId: temp.departmentId, path: temp.departmentName }];
        this.props.form.setFieldsValue({
          company: company,
          department: department,
        });
      })
      .catch(err => {
        message.error('请求失败，请稍后重试...');
      });
  };

  userChange = user => {
    service
      .getUserInfoByTypeId(user.userOid)
      .then(res => {
        let temp = res.data;
        let company = [{ id: temp.companyId, name: temp.companyName }];
        let department = [{ departmentId: temp.departmentId, path: temp.departmentName }];
        this.props.form.setFieldsValue(
          {
            company: company,
            department: department,
          },
          () => {
            this.userOrCompanyOrUnitChange('oldId', 'oldId', user.id);
          }
        );
      })
      .catch(err => {
        message.error('请求失败，请稍后重试...');
      });
  };

  unitChange = value => {
    this.userOrCompanyOrUnitChange('oldId', value[0].departmentId, 'oldId');
  };

  userOrCompanyOrUnitChange = (companyId, unitId, userId) => {
    let oldCompany = this.props.form.getFieldValue('company');
    let oldUnit = this.props.form.getFieldValue('department');
    let oldUser = this.props.form.getFieldValue('user');
    if (companyId === 'oldId') {
      companyId = oldCompany[0].id;
    }
    if (unitId === 'oldId') {
      unitId = oldUnit[0].departmentId;
    }
    if (userId === 'oldId') {
      userId = oldUser.id;
    }
    this.setDimension(this.state.dimensionList, companyId, unitId, userId);
  };

  getDimensionValues = (open, item) => {
    let { dimensionValues } = this.state;
    if (open && !dimensionValues[item.dimensionId]) {
      service
        .getDimensionValues(item.dimensionId, this.state.contractParams.companyId)
        .then(res => {
          this.setState({ dimensionValues: { ...dimensionValues, [item.dimensionId]: res.data } });
        })
        .catch(err => {
          message.error(err.response.data.message);
        });
    }
  };

  render() {
    const { getFieldDecorator } = this.props.form;

    const rowLayout = { type: 'flex', gutter: 24, justify: 'center' };
    const formItemLayout = {
      labelCol: {
        xs: { span: 12 },
        sm: { span: 6 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 16 },
      },
    };

    const {
      pageLoading,
      loading,
      isNew,
      currencyList,
      contractParams,
      dimensionList,
      applicationTypeInfo,
      fileList,
      model,
      defaultUser,
      select_contract,
      contractShow,
      contractId,
    } = this.state;

    return (
      <div className="new-contract" style={{ marginBottom: 60, marginTop: 10 }}>
        <Spin spinning={pageLoading}>
          {!pageLoading && (
            <Form onSubmit={this.handleSave}>
              <Row {...rowLayout}>
                <Col span={10}>
                  <FormItem label={this.$t('expense.reverse.apply.name')} {...formItemLayout}>
                    {/*申请人*/}
                    {getFieldDecorator('user', {
                      rules: [
                        { required: true, message: this.$t('expense.please.select.a') },
                      ] /*请选择*/,
                      initialValue: isNew
                        ? { id: defaultUser.id, fullName: defaultUser.fullName }
                        : { id: model.employeeId, fullName: model.employeeName },
                    })(
                      <Lov
                        code="application_user_authorize"
                        valueKey="id"
                        labelKey="fullName"
                        onChange={this.userChange}
                        allowClear={false}
                        single
                        extraParams={{ applicationTypeId: this.props.match.params.typeId }}
                      />
                    )}
                  </FormItem>
                </Col>
              </Row>
              <Row {...rowLayout}>
                <Col span={10}>
                  <FormItem label={this.$t('expense.the.company')} {...formItemLayout}>
                    {/*公司*/}
                    {getFieldDecorator('company', {
                      rules: [{ required: true, message: this.$t('common.please.select') }],
                      initialValue: isNew
                        ? [{ id: this.props.user.companyId, name: this.props.user.companyName }]
                        : [{ id: model.companyId, name: model.companyName }],
                    })(
                      <Chooser
                        type="company"
                        labelKey="name"
                        valueKey="id"
                        disabled={!isNew}
                        single
                        listExtraParams={{ setOfBooksId: this.props.company.setOfBooksId }}
                        onChange={this.companyChange}
                        showClear={false}
                      />
                    )}
                  </FormItem>
                </Col>
              </Row>
              <Row {...rowLayout}>
                <Col span={10}>
                  <FormItem label={this.$t('expense.department')} {...formItemLayout}>
                    {/*部门*/}
                    {getFieldDecorator('department', {
                      rules: [{ required: true, message: this.$t('common.please.select') }],
                      initialValue: isNew
                        ? [
                            {
                              departmentId: this.props.user.departmentId,
                              path: this.props.user.departmentName,
                            },
                          ]
                        : [
                            {
                              departmentId: model.departmentId,
                              path: model.departmentName,
                            },
                          ],
                    })(
                      <Chooser
                        type="department_document"
                        labelKey="path"
                        valueKey="departmentId"
                        single
                        showClear={false}
                        onChange={this.unitChange}
                        listExtraParams={{ tenantId: this.props.user.tenantId }}
                      />
                    )}
                  </FormItem>
                </Col>
              </Row>
              <Row {...rowLayout}>
                <Col span={10}>
                  <FormItem label={this.$t('expense.policy.currencyName')} {...formItemLayout}>
                    {/*币种*/}
                    {getFieldDecorator('currencyCode', {
                      rules: [{ required: true, message: this.$t('common.please.select') }],
                      initialValue: isNew ? this.props.company.baseCurrency : model.currencyCode,
                    })(
                      <Select disabled={!isNew} onChange={this.currencyChange}>
                        {currencyList.map(item => {
                          return (
                            <Select.Option key={item.currency} value={item.currency}>
                              {item.currency}-{item.currencyName}
                            </Select.Option>
                          );
                        })}
                      </Select>
                    )}
                  </FormItem>
                </Col>
              </Row>
              {dimensionList.filter(item => item.headerFlag).map(item => {
                return (
                  <Row key={item.dimensionId} {...rowLayout}>
                    <Col span={10}>
                      <FormItem label={item.name} {...formItemLayout}>
                        {getFieldDecorator('dimension-' + item.dimensionId, {
                          rules: [
                            {
                              required: item.requiredFlag,
                              message: this.$t('common.please.select'),
                            },
                          ],
                          initialValue: item.value ? item.value : undefined,
                        })(
                          <Select allowClear={true} placeholder={this.$t('common.please.select')}>
                            {item.options.map(option => {
                              return (
                                <Select.Option key={option.id}>
                                  {option.dimensionItemName}
                                </Select.Option>
                              );
                            })}
                          </Select>
                        )}
                      </FormItem>
                    </Col>
                  </Row>
                );
              })}
              {(applicationTypeInfo.associateContract || (!isNew && model.associateContract)) && (
                <Row {...rowLayout}>
                  <Col span={10}>
                    <FormItem
                      label={this.$t('expense.associated.with.the.contract')}
                      {...formItemLayout}
                    >
                      {/*关联合同*/}
                      {getFieldDecorator('contract', {
                        rules: [
                          {
                            required: isNew ? applicationTypeInfo.requireInput : model.requireInput,
                            message: this.$t('common.please.select'),
                          },
                        ],
                        initialValue: isNew
                          ? []
                          : model.contractNumber
                            ? [
                                {
                                  contractNumber: model.contractNumber,
                                  contractHeaderId: model.contractHeaderId,
                                },
                              ]
                            : [],
                      })(
                        <Chooser
                          // type="select_contract"
                          labelKey="contractNumber"
                          valueKey="contractHeaderId"
                          single={true}
                          listExtraParams={contractParams}
                          selectorItem={select_contract}
                        />
                      )}
                    </FormItem>
                  </Col>
                </Row>
              )}
              <Row {...rowLayout}>
                <Col span={10}>
                  <FormItem label={this.$t('expense.reverse.remark')} {...formItemLayout}>
                    {/*备注*/}
                    {getFieldDecorator('remarks', {
                      initialValue: isNew ? '' : model.remarks,
                    })(<Input.TextArea autosize={{ minRows: 3 }} />)}
                  </FormItem>
                </Col>
              </Row>
              <Row {...rowLayout} style={{ marginBottom: 40 }}>
                <Col span={10}>
                  <FormItem
                    label={this.$t('expense.the.attachment.information')}
                    {...formItemLayout}
                  >
                    {/*附件信息*/}
                    {getFieldDecorator('attachmentOID')(
                      <Upload
                        attachmentType="BUDGET_JOURNAL"
                        uploadUrl={`${config.baseUrl}/api/upload/static/attachment`}
                        fileNum={9}
                        uploadHandle={this.handleUpload}
                        defaultFileList={fileList}
                        defaultOids={isNew ? [] : model.attachmentOidList}
                      />
                    )}
                  </FormItem>
                </Col>
              </Row>
              <div
                style={{
                  position: 'fixed',
                  bottom: 0,
                  marginLeft: '-35px',
                  width: '100%',
                  height: '50px',
                  boxShadow: '0px -5px 5px rgba(0, 0, 0, 0.067)',
                  background: '#fff',
                  lineHeight: '50px',
                }}
              >
                <Button
                  type="primary"
                  htmlType="submit"
                  loading={loading}
                  style={{ margin: '0 20px' }}
                >
                  {isNew ? this.$t('expense.the.next.step') : this.$t('expense.determine')}
                </Button>
                {isNew ? (
                  <Button onClick={this.onBack}>{this.$t('expense.cancel')}</Button>
                ) : (
                  <Button onClick={this.onBack}>{this.$t('expense.return')}</Button>
                )}
              </div>
            </Form>
          )}
        </Spin>
        <Modal
          title={this.$t('expense.the.contract.details')} /*合同详情*/
          visible={contractShow}
          onCancel={() => {
            this.setState({ contractShow: false });
          }}
          width="90%"
          bodyStyle={{
            maxHeight: '70vh',
            overflow: 'auto',
            padding: '0 10px',
          }}
          footer={null}
        >
          <ContractDetail id={contractId} isApprovePage={true} />
        </Modal>
      </div>
    );
  }
}

function map(state) {
  return {
    user: state.user.currentUser,
    company: state.user.company,
  };
}

export default connect(map)(Form.create()(NewExpenseApplicationFrom));
