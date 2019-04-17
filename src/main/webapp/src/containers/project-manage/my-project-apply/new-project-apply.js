import React, { Component } from 'react';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import { Form, Input, Row, Col, Button, Affix, Select, message } from 'antd';
import Upload from 'widget/upload';
import config from 'config';
import Chooser from 'widget/chooser';
import PropTypes from 'prop-types';
import service from './service';

const FormItem = Form.Item;
const bottomStyle = {
  position: 'fixed',
  bottom: 0,
  width: '100%',
  height: '50px',
  boxShadow: '0px -5px 5px rgba(0, 0, 0, 0.15)',
  background: '#fff',
  lineHeight: '50px',
  paddingLeft: '20px',
  zIndex: 1,
};

class NewProjectApply extends Component {
  constructor(props) {
    super(props);
    // isNew 属性判断 本组件是新增页面 还是 编辑侧滑框,同时渲染不同的样式及按钮组
    this.state = {
      uploadOIDs: [], // 附件oids
      projectTypeList: [], // 项目类型
      rate: 1, // 汇率
      // 币种：默认为本位币；是否显示，是否必输通过项目申请单类型定义“金额设置”控制
      // 申请金额：可手工输入；是否显示，是否必输通过项目申请单类型定义“金额设置”控制
      // isRequired: false, // 是否必输
      isRequired: true,
      // isView: true, // 是否显示
      isView: true,
      saveLoading: false,
    };
  }

  componentDidMount = () => {
    // 获取币种列表
    // const { company } = this.props;
    // service.getCurrencyList(company.companyOid).then(res => {
    //   this.setState({ currencyList: res.data });
    // });
    const { params } = this.props;

    const fileList = [];
    if (params.id && params.attachments && params.attachments.length) {
      //  eslint-disable-next-line array-callback-return
      params.attachments.map(item => {
        fileList.push({
          ...item,
          uid: item.attachmentOid,
          name: item.fileName,
          status: 'done',
        });
      });
    }

    this.setState({ fileList });

    this.getCurProjectDetails();
  };

  // 获取当前项目类型详情
  getCurProjectDetails = () => {
    let applicationTypeId;
    const { isNew, match, params } = this.props;
    if (isNew) {
      applicationTypeId = match.params.id;
    } else {
      applicationTypeId = params.proReqTypeId;
    }
    service.getCurProjectDetail(applicationTypeId).then(res => {
      this.setState({
        projectTypeList: res.data,
        isRequired: res.data.amountNullFlag,
        isView: res.data.amountFlag,
      });
    });
  };

  // 获取汇率
  getExchangeRate = values => {
    service
      .getCurrencyExchangeRate(values.key)
      .then(res => {
        this.setState({ rate: res.data.rate });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  // 上传附件
  handleUpload = OIDs => {
    this.setState({
      uploadOIDs: OIDs,
    });
  };

  // 表单提交
  handleSave = e => {
    e.preventDefault();
    let param = {};
    const { form, match, isNew, user, company, allInfo } = this.props;
    if (!isNew) this.setState({ saveLoading: true });
    form.validateFields((err, values) => {
      if (err) return;
      const { uploadOIDs, rate } = this.state;
      let handleMethod = null;
      let finalParams = null;
      param = {
        ...values,
        rate, // 汇率
        attachmentOid: uploadOIDs.length > 0 ? uploadOIDs.join() : null, // 附件
        currencyCode: values.currencyCode ? values.currencyCode.key : '-', // 币种
        departmentId: values.departmentId[0].departmentId, // 部门id
        companyId: values.companyId[0].id, // 公司id
        pmId: values.pmId[0].id, // 项目负责人id
        parentProReqId:
          (values.parentProReqId &&
            values.parentProReqId.length > 0 &&
            values.parentProReqId[0].id) ||
          '',
        customerId:
          (values.customerId && values.customerId.length > 0 && values.customerId[0].id) || '',
        tenantId: user.tenantId, // 租户id
        setOfBooksId: company.setOfBooksId, // 账套id,
        dataOrigin: 'CREATE_BY_HAND',
        dataOriginSystem: 'HEC',
        employeeId: values.employeeId[0].id,
      };
      if (isNew) {
        handleMethod = service.saveProjectApplyValue;
        param.proReqTypeId = match.params.id; // 项目申请单类型id
        finalParams = { ...param };
      } else {
        handleMethod = service.updateProjectApplyValue;
        allInfo.projectRequisition = { ...allInfo.projectRequisition, ...param };
        finalParams = { ...allInfo };
      }

      if (handleMethod) {
        handleMethod(finalParams)
          .then(res => {
            if (res) {
              message.success(this.$t('structure.saveSuccess'));
              if (isNew) {
                this.jumpToNextPage(res.data.id);
              } else {
                this.setState({ saveLoading: false }, () => {
                  const { onClose } = this.props;
                  if (onClose) onClose(true);
                });
              }
            }
          })
          .catch(error => {
            message.error(error.response.data.message);
            if (!isNew) this.setState({ saveLoading: false });
          });
      }
    });
  };

  // 新增下的跳转至下一页面
  jumpToNextPage = id => {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/project-manage/my-project-apply/project-apply-details/${id}`,
      })
    );
  };

  // 返回
  handleReturn = () => {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/project-manage/my-project-apply/my-project-apply`,
      })
    );
  };

  // -----编辑侧滑框下函数-------

  handleEdit = e => {
    this.handleSave(e);
  };

  // 关闭模态框
  handleCancel = () => {
    const { onClose } = this.props;
    if (onClose) onClose();
  };
  // ------------------

  render() {
    const { projectTypeList, saveLoading, fileList } = this.state;
    const { form, user, company, isNew, params, show } = this.props;
    const { getFieldDecorator } = form;
    const rowLayout = { type: 'flex', gutter: 24, justify: 'center' };
    const formItemLayout = isNew
      ? {
          labelCol: {
            xs: { span: 12 },
            sm: { span: 8 },
          },
          wrapperCol: {
            xs: { span: 24 },
            sm: { span: 15 },
          },
        }
      : {
          labelCol: { span: 8 },
          wrapperCol: { span: 10 },
        };
    const colSpan = isNew ? 12 : 24;
    return (
      <div className="new-project-apply">
        <Form>
          <Row {...rowLayout}>
            <Col span={colSpan}>
              {/* 申请人 */}
              <FormItem {...formItemLayout} label={this.$t('common.applicant')}>
                {getFieldDecorator('employeeId', {
                  rules: [{ required: true }],
                  initialValue:
                    isNew || !params.employeeId
                      ? [{ name: user.userName, id: user.id }]
                      : [{ name: params.employeeName, id: params.employeeId }],
                })(
                  <Chooser
                    type="select_employee"
                    labelKey="name"
                    valueKey="id"
                    single
                    disabled={!isNew}
                    placeholder="请选择项目负责人"
                  />
                )}
              </FormItem>
            </Col>
          </Row>
          <Row {...rowLayout}>
            <Col span={colSpan}>
              <FormItem label={this.$t('acp.company')} {...formItemLayout}>
                {getFieldDecorator('companyId', {
                  rules: [{ required: true, message: this.$t('common.please.select') }],
                  initialValue: isNew
                    ? [{ id: company.id, name: company.name }]
                    : [{ id: params.companyId || '', name: params.companyName || '' }],
                })(
                  <Chooser
                    type="available_company_setOfBooks"
                    labelKey="name"
                    valueKey="id"
                    single
                    disabled={!isNew}
                    listExtraParams={{ setOfBooksId: company.setOfBooksId }}
                    showClear={false}
                  />
                )}
              </FormItem>
            </Col>
          </Row>
          <Row {...rowLayout}>
            <Col span={colSpan}>
              <FormItem {...formItemLayout} label={this.$t('common.department')}>
                {getFieldDecorator('departmentId', {
                  rules: [
                    {
                      required: true,
                      message: this.$t({ id: 'common.please.select' }),
                    },
                  ],
                  initialValue: isNew
                    ? [
                        {
                          path: user.departmentPath,
                          departmentId: user.departmentId,
                        },
                      ]
                    : [
                        {
                          path: params.departmentName || '',
                          departmentId: params.departmentId || '',
                        },
                      ],
                })(
                  <Chooser
                    type="department_document"
                    labelKey="path"
                    valueKey="departmentId"
                    single
                    disabled={!isNew}
                    listExtraParams={{ tenantId: user.tenantId }}
                  />
                )}
              </FormItem>
            </Col>
          </Row>
          <Row {...rowLayout}>
            <Col span={colSpan}>
              <FormItem {...formItemLayout} label="项目名称">
                {getFieldDecorator('projectName', {
                  rules: [
                    {
                      required: true,
                      message: this.$t('common.enter'),
                    },
                  ],
                  initialValue: params.projectName || '',
                })(<Input placeholder="请输入项目名称" />)}
              </FormItem>
            </Col>
          </Row>
          <Row {...rowLayout}>
            <Col span={colSpan}>
              <FormItem {...formItemLayout} label="项目负责人">
                {getFieldDecorator('pmId', {
                  rules: [
                    {
                      required: true,
                      message: this.$t({ id: 'common.please.select' }),
                    },
                  ],
                  initialValue: isNew
                    ? undefined
                    : [{ name: params.pmName || '', id: params.pmId || '' }],
                })(
                  <Chooser
                    type="select_employee"
                    labelKey="name"
                    valueKey="id"
                    single
                    placeholder="请选择项目负责人"
                  />
                )}
              </FormItem>
            </Col>
          </Row>
          <Row {...rowLayout}>
            <Col span={colSpan}>
              <FormItem {...formItemLayout} label="项目地">
                {getFieldDecorator('projectLocation', {
                  initialValue: params.projectLocation || '',
                })(<Input placeholder="请输入项目地" />)}
              </FormItem>
            </Col>
          </Row>
          <Row {...rowLayout}>
            <Col span={colSpan}>
              <FormItem {...formItemLayout} label="项目编号">
                {getFieldDecorator('projectNumber', {
                  rules: [
                    {
                      required: true,
                      message: this.$t({ id: 'common.enter' }),
                    },
                  ],
                  initialValue: isNew ? '' : params.projectNumber,
                })(<Input placeholder="请输入项目编号" />)}
              </FormItem>
            </Col>
          </Row>
          <Row {...rowLayout}>
            <Col span={colSpan}>
              <FormItem {...formItemLayout} label="父项目申请单编号">
                {getFieldDecorator('parentProReqId', {
                  initialValue:
                    isNew || !params.parentProReqId
                      ? undefined
                      : [
                          {
                            projectReqNumber: params.parentProReqNumber,
                            id: params.parentProReqId,
                          },
                        ],
                })(
                  <Chooser
                    type="select_parent_project"
                    labelKey="projectReqNumber"
                    valueKey="id"
                    single
                    listExtraParams={{
                      tenantId: user.tenantId,
                      setOfBooksId: company.setOfBooksId,
                    }}
                    placeholder="请选择项目申请单编号"
                  />
                )}
              </FormItem>
            </Col>
          </Row>
          <Row {...rowLayout}>
            <Col span={colSpan}>
              <FormItem {...formItemLayout} label="项目类型">
                {getFieldDecorator('projectTypeId', {
                  rules: [
                    {
                      required: true,
                      message: this.$t({ id: 'common.enter' }),
                    },
                  ],
                  initialValue: params.projectTypeId || '',
                })(
                  <Select
                    placeholder="请选择项目类型"
                    getPopupContainer={triggerNode => triggerNode.parentNode}
                  >
                    {projectTypeList.length > 0 &&
                      projectTypeList.map(type => {
                        return <Select.Option key={type.id}>{type.projectTypeName}</Select.Option>;
                      })}
                  </Select>
                )}
              </FormItem>
            </Col>
          </Row>
          <Row {...rowLayout}>
            <Col span={colSpan}>
              <FormItem {...formItemLayout} label="客户">
                {getFieldDecorator('customerId', {
                  initialValue:
                    isNew || !params.customerId
                      ? undefined
                      : [{ name: params.customerName, id: params.customerId }],
                })(
                  <Chooser
                    type="select_employee"
                    labelKey="name"
                    valueKey="id"
                    single
                    placeholder="请输入客户"
                  />
                )}
              </FormItem>
            </Col>
          </Row>
          {/*<Row {...rowLayout}>*/}
          {/*<Col span={colSpan}>*/}
          {/*{String(isView) === 'true' ? (*/}
          {/*<FormItem {...formItemLayout} label="申请金额">*/}
          {/*{getFieldDecorator('amount', {*/}
          {/*rules: [*/}
          {/*{*/}
          {/*required: isRequired,*/}
          {/*message: this.$t({ id: 'common.enter' }),*/}
          {/*},*/}
          {/*],*/}
          {/*initialValue: params.amount || '',*/}
          {/*})(<Input placeholder="请输入参考项目编号" />)}*/}
          {/*</FormItem>*/}
          {/*) : (*/}
          {/*<div />*/}
          {/*)}*/}
          {/*</Col>*/}
          {/*</Row>*/}
          {/*<Row {...rowLayout}>*/}
          {/*<Col span={colSpan}>*/}
          {/*{String(isView) === 'true' ? (*/}
          {/*<FormItem {...formItemLayout} label={this.$t('acp.currency')}>*/}
          {/*{getFieldDecorator('currencyCode', {*/}
          {/*rules: [*/}
          {/*{*/}
          {/*required: isRequired,*/}
          {/*message: this.$t({ id: 'common.please.select' }),*/}
          {/*},*/}
          {/*],*/}
          {/*initialValue: isNew*/}
          {/*? {*/}
          {/*key: company.baseCurrency,*/}
          {/*label: `${company.baseCurrency}-${company.baseCurrencyName}`,*/}
          {/*}*/}
          {/*: {*/}
          {/*key: params.currencyCode,*/}
          {/*label: `${params.currencyCode}-${params.currencyName}`,*/}
          {/*},*/}
          {/*})(*/}
          {/*<Select*/}
          {/*placeholder={this.$t('common.please.select')}*/}
          {/*onChange={this.getExchangeRate}*/}
          {/*labelInValue*/}
          {/*getPopupContainer={triggerNode => triggerNode.parentNode}*/}
          {/*>*/}
          {/*{currencyList.map(item => {*/}
          {/*return (*/}
          {/*<Select.Option key={item.currency}>*/}
          {/*{item.currency}-{item.currencyName}*/}
          {/*</Select.Option>*/}
          {/*);*/}
          {/*})}*/}
          {/*</Select>*/}
          {/*)}*/}
          {/*</FormItem>*/}
          {/*) : (*/}
          {/*<div />*/}
          {/*)}*/}
          {/*</Col>*/}
          {/*</Row>*/}
          <Row {...rowLayout}>
            <Col span={colSpan}>
              <FormItem label="项目说明" {...formItemLayout}>
                {getFieldDecorator('projectDes', {
                  initialValue: params.projectDes || '',
                })(<Input.TextArea autosize={{ minRows: 3 }} placeholder="请输入项目说明" />)}
              </FormItem>
            </Col>
          </Row>
          <Row {...rowLayout} style={{ marginBottom: 80 }}>
            <Col span={colSpan}>
              {show && (
                <FormItem label={this.$t('acp.fileInfo')} {...formItemLayout}>
                  {getFieldDecorator('attachmentOid')(
                    <Upload
                      attachmentType="BUDGET_JOURNAL"
                      uploadUrl={`${config.baseUrl}/api/upload/static/attachment`}
                      fileNum={9}
                      defaultFileList={fileList || []}
                      uploadHandle={this.handleUpload}
                      defaultOids={params.attachmentOidList || []}
                    />
                  )}
                </FormItem>
              )}
            </Col>
          </Row>
          {isNew ? (
            <Affix style={bottomStyle}>
              <Button onClick={this.handleReturn} style={{ marginRight: '10px' }}>
                {/* 返回 */}
                {this.$t('common.return')}
              </Button>
              <Button type="primary" onClick={this.handleSave}>
                {/* 下一步 */}
                {this.$t('acp.next')}
              </Button>
            </Affix>
          ) : (
            <div className="slide-footer">
              <Button
                className="btn"
                type="primary"
                htmlType="submit"
                loading={saveLoading}
                onClick={this.handleEdit}
              >
                {this.$t('common.save')}
              </Button>
              <Button className="btn" onClick={this.handleCancel}>
                {this.$t('common.cancel')}
              </Button>
            </div>
          )}
        </Form>
      </div>
    );
  }
}

NewProjectApply.propTypes = {
  isNew: PropTypes.bool, // 是否是新建
  params: PropTypes.object, // 初始参数
};

NewProjectApply.defaultProps = {
  isNew: true,
  params: {},
};

function map(state) {
  return {
    user: state.user.currentUser,
    company: state.user.company,
  };
}

export default connect(map)(Form.create('new')(NewProjectApply));
