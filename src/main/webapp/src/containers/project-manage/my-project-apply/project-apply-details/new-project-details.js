import React, { Component } from 'react';
import { Form, Row, Col, DatePicker, Divider, Input, Radio, Button, message } from 'antd';
import moment from 'moment';
import { connect } from 'dva';
import ListSelector from 'widget/new-list-selector';
import detailsService from './details-service';

const FormItem = Form.Item;
const RadioGroup = Radio.Group;

class NewProjectDetails extends Component {
  constructor(props) {
    super(props);
    this.state = {
      // 控制visibleUserScope
      visibleUserScope: 1001,
      // 人员权限弹窗是否显示
      releaseIdsVisible: false,
      // 人员权限弹窗类型
      releaseIdsType: 'company',
      // 人员权限弹窗参数
      releaseIdsExtraParams: {},
      // 人员权限弹窗已选择数据
      releaseIdsSelectedData: [],
      // 人员权限弹窗选择数据
      releaseIdsList: [],
      // 弹窗内选中的值的label
      lineLabel: {
        1002: 'fullName', // 人员
        1003: 'name', // 部门
        1004: 'name', // 公司
      },
      arrLabel: {
        1002: 'userIdList',
        1003: 'departmentIdList',
        1004: 'companyIdList',
      },
      _arrLabel: {
        1002: 'userList',
        1003: 'departmentList',
        1004: 'companyList',
      },
      saveLoading: false,
    };
  }

  componentDidMount = () => {
    const { params } = this.props;
    const { _arrLabel } = this.state;
    if (params.projectRequisition && params.projectRequisition.startUseDate) {
      const { applyEmployee } = params.projectRequisition;
      if (applyEmployee) {
        // 存在applyEmployee且不为1001，则必然存在与之关联的数据
        // 编辑状态下为权限部分赋予初值
        const nowReleaseIdsList = [];
        if (Number(applyEmployee) !== 1001) {
          params[_arrLabel[applyEmployee]].forEach(item => {
            // if (Number(applyEmployee) === 1003) {
            //   nowReleaseIdsList.push({ key: item.id, departmentId: item.id, fullName: item.id });
            // } else {
            //   nowReleaseIdsList.push({ key: item.id, name: item.name, id: item.id });
            // }
            if (Number(applyEmployee) === 1002) {
              nowReleaseIdsList.push({ key: item.id, id: item.id, fullName: item.id });
            } else {
              nowReleaseIdsList.push({ key: item.id, name: item.name, id: item.id });
            }
          });
        }
        this.setState({
          visibleUserScope: Number(applyEmployee),
          releaseIdsList: nowReleaseIdsList,
        });
      }
    }
  };

  // 根据radio选择的值联动input与listSelector组件
  onVisibleUserScopeChange = e => {
    this.setState({
      visibleUserScope: e.target.value,
      releaseIdsList: [],
    });
  };

  // 聚焦input框可见listSelector组件
  onReleaseIdsClick = e => {
    e.preventDefault();
    const { visibleUserScope, releaseIdsList } = this.state;
    let { releaseIdsType, releaseIdsExtraParams } = this.state;
    const { company } = this.props;
    switch (visibleUserScope) {
      case 1002:
        //  按人员添加
        // releaseIdsType = 'contract_user';
        releaseIdsType = 'select_setOfBooksId_employee';
        // releaseIdsExtraParams = { companyId: company.id };
        releaseIdsExtraParams = {};
        break;
      case 1003:
        //  按部门添加
        // releaseIdsType = 'deptCode';
        releaseIdsType = 'select_setOfBooksId_department';
        releaseIdsExtraParams = {};
        break;
      case 1004:
        //  按公司添加
        releaseIdsType = 'company';
        releaseIdsExtraParams = { setOfBooksId: company.setOfBooksId };
        break;
      default:
        releaseIdsType = '';
        releaseIdsExtraParams = {};
    }
    this.setState({
      releaseIdsVisible: true,
      releaseIdsSelectedData: releaseIdsList,
      releaseIdsType,
      releaseIdsExtraParams,
    });
  };

  // 关闭listSelector组件
  onReaseIdsCancel = () => {
    this.setState({ releaseIdsVisible: false });
  };

  // listSelector确认选择
  handleReleaseIdsOk = values => {
    let { releaseIdsList } = this.state;
    releaseIdsList = values.result;
    this.setState({
      releaseIdsList,
      releaseIdsVisible: false,
    });
  };

  // 保存
  handleSubmitSave = e => {
    e.preventDefault();
    const { form, detailsAllParams } = this.props;
    const { validateFieldsAndScroll } = form;
    const { releaseIdsList, visibleUserScope, arrLabel } = this.state;
    this.setState({ saveLoading: true });
    validateFieldsAndScroll((err, values) => {
      if (err) return;
      if (visibleUserScope !== 1001 && releaseIdsList.length < 1) {
        message.error('请至少选择一条数据');
        return;
      }
      const ids = releaseIdsList.map(item => {
        if (visibleUserScope === 1003) {
          // return item.departmentId;
          return item.id;
        } else if (visibleUserScope === 1002) {
          return item.id;
        } else if (visibleUserScope === 1004) {
          return item.id;
        } else return [];
      });
      const params = {
        // moment： 当传入undefined值时，自动获取系统时间
        startUseDate: moment(values.startUseDate),
        closeUseDate: values.closeUseDate ? moment(values.closeUseDate) : '',
        projectStartDate: values.projectStartDate ? moment(values.projectStartDate) : '',
        projectCloseDate: values.projectCloseDate ? moment(values.projectCloseDate) : '',
        // headerId, // 项目申请单详情中的id
        // projectReqNumber, // 项目申请单编号
        applyEmployee: String(visibleUserScope), // 1001,1002,1003,1004
      };
      const { projectRequisition } = detailsAllParams;
      detailsAllParams.projectRequisition = { ...projectRequisition, ...params };
      detailsAllParams[arrLabel[visibleUserScope]] = [...ids];
      // return;
      detailsService
        .saveDetailsValues(detailsAllParams)
        .then(res => {
          if (res) {
            this.setState({ saveLoading: false }, () => {
              const { onClose } = this.props;
              onClose(true);
            });
          }
        })
        .catch(error => {
          message.error(error.response.data.message);
          this.setState({ saveLoading: false });
        });
    });
  };

  // 关闭侧滑框
  handleCancel = () => {
    const { onClose } = this.props;
    onClose();
  };

  render() {
    const { form, params } = this.props;
    const data = params.projectRequisition;
    const { getFieldDecorator } = form;
    const formItemLayout = {
      labelCol: { span: 9 },
      wrapperCol: { span: 10 },
    };
    const {
      visibleUserScope,
      releaseIdsVisible,
      releaseIdsType,
      releaseIdsExtraParams,
      releaseIdsSelectedData,
      releaseIdsList,
      lineLabel,
      saveLoading,
    } = this.state;
    return (
      <div>
        <Form>
          <h3>日期信息</h3>
          <Row>
            <Col span={12}>
              <FormItem {...formItemLayout} label="启用日期">
                {getFieldDecorator('startUseDate', {
                  rules: [{ required: true, message: this.$t('common.please.select') }],
                  initialValue:
                    data && data.startUseDate ? moment(data.startUseDate) : moment(new Date()),
                })(<DatePicker placeholder="请选择" />)}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem {...formItemLayout} label="禁用日期">
                {getFieldDecorator('closeUseDate', {
                  initialValue: data && data.closeUseDate ? moment(data.closeUseDate) : undefined,
                })(<DatePicker placeholder="请选择" />)}
              </FormItem>
            </Col>
          </Row>
          <Row>
            <Col span={12}>
              <FormItem {...formItemLayout} label="项目实际开始日期">
                {getFieldDecorator('projectStartDate', {
                  initialValue:
                    data && data.projectStartDate ? moment(data.projectStartDate) : undefined,
                })(<DatePicker placeholder="请选择" />)}
              </FormItem>
            </Col>
            <Col span={12}>
              <FormItem {...formItemLayout} label="项目实际结束日期">
                {getFieldDecorator('projectCloseDate', {
                  initialValue:
                    data && data.projectCloseDate ? moment(data.projectCloseDate) : undefined,
                })(<DatePicker placeholder="请选择" />)}
              </FormItem>
            </Col>
          </Row>
          <Divider style={{ margin: '20px' }} />
          <h3>权限设置</h3>
          <Row>
            <Col span={6} style={{ textAlign: 'right' }}>
              适用人员：
            </Col>
            <Col span={18}>
              <RadioGroup value={visibleUserScope} onChange={this.onVisibleUserScopeChange}>
                <Radio value={1001}>{this.$t('common.all.user')}</Radio>
                <Radio value={1002}>{this.$t('common.add.by.users')}</Radio>
                <Radio value={1003}>{this.$t('common.add.by.department')}</Radio>
                <Radio value={1004}>{this.$t('common.add.by.companys')}</Radio>
              </RadioGroup>
            </Col>
          </Row>
          <Row>
            <Col span={12} push={6} style={{ marginTop: '10px', marginBottom: '30px' }}>
              {visibleUserScope === 1001 ? (
                <div />
              ) : (
                <Input
                  onClick={this.onReleaseIdsClick}
                  disabled={visibleUserScope === 1001}
                  value={
                    visibleUserScope === 1001
                      ? this.$t('common.all.user')
                      : this.$t('contract.select.data', { total: releaseIdsList.length })
                  }
                />
              )}
            </Col>
          </Row>
          <div className="slide-footer" style={{ backgroundColor: 'white' }}>
            <Button
              type="primary"
              htmlType="submit"
              loading={saveLoading}
              onClick={this.handleSubmitSave}
            >
              {this.$t('common.save')}
            </Button>
            <Button onClick={this.handleCancel}>{this.$t('common.cancel')}</Button>
          </div>
        </Form>
        {/* 人员权限上的弹窗 */}
        <ListSelector
          visible={releaseIdsVisible}
          onCancel={this.onReaseIdsCancel}
          type={releaseIdsType}
          extraParams={releaseIdsExtraParams}
          selectedData={[...releaseIdsSelectedData]}
          labelKey={lineLabel[visibleUserScope]}
          onOk={this.handleReleaseIdsOk}
        />
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    company: state.user.company,
  };
}
export default connect(mapStateToProps)(Form.create()(NewProjectDetails));
