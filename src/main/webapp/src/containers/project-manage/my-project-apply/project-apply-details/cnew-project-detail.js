import React, { Component } from 'react';
import { Form, Row, Col, DatePicker, Divider, Button, message } from 'antd';
import moment from 'moment';
import { connect } from 'dva';
import PermissionsSetting from 'components/Widget/Template/permissions-setting';
import detailsService from './details-service';

const FormItem = Form.Item;

class CNewProjectDetails extends Component {
  constructor(props) {
    super(props);
    this.state = {
      permissions: {
        type: 'all',
        values: [],
      },
      label: {
        1001: 'all',
        1002: 'employee',
        1003: 'department',
        1004: 'company',
      },
      reLabel: {
        all: '1001',
        employee: '1002',
        department: '1003',
        company: '1004',
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
    };
  }

  componentDidMount = () => {
    const { params } = this.props;
    const { _arrLabel, label } = this.state;
    if (params.projectRequisition && params.projectRequisition.startUseDate) {
      const { applyEmployee } = params.projectRequisition;
      if (applyEmployee) {
        // 存在applyEmployee且不为1001，则必然存在与之关联的数据
        // 编辑状态下为权限部分赋予初值
        const nowReleaseIdsList = [];
        if (Number(applyEmployee) !== 1001) {
          params[_arrLabel[applyEmployee]].forEach(item => {
            if (Number(applyEmployee) === 1002) {
              nowReleaseIdsList.push({ key: item.id, id: item.id, fullName: item.id });
            } else {
              nowReleaseIdsList.push({ key: item.id, name: item.name, id: item.id });
            }
          });
        }
        this.setState({
          permissions: {
            type: label[applyEmployee],
            values: [...nowReleaseIdsList],
          },
        });
      }
    }
  };

  // 保存
  handleSubmitSave = e => {
    e.preventDefault();
    const { form } = this.props;
    const { validateFieldsAndScroll } = form;

    validateFieldsAndScroll((err, values) => {
      if (err) return;
      const detailsAllParams = this.dealWidthParams(values);
      if (!detailsAllParams) return;
      this.setState({ saveLoading: true });
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

  // 处理将要执行保存的数据
  dealWidthParams = values => {
    const { detailsAllParams } = this.props;
    const { arrLabel, reLabel, _arrLabel } = this.state;
    const visibleUserScope =
      values.userOrCompanyOrDepList && reLabel[values.userOrCompanyOrDepList.type];
    const userOrCompanyOrDepList =
      values.userOrCompanyOrDepList && values.userOrCompanyOrDepList.values;

    // 校验
    if (String(visibleUserScope) !== '1001' && userOrCompanyOrDepList.length < 1) {
      message.error('请至少选择一条数据');
      return;
    }
    // 处理id数组
    const ids = userOrCompanyOrDepList.map(item => {
      if (Number(visibleUserScope) === 1003) {
        return item.value;
      } else if (Number(visibleUserScope) === 1002) {
        return item.id;
      } else if (Number(visibleUserScope) === 1004) {
        return item.id;
      } else return [];
    });

    // 对应将要给后台数据的字段格式进行赋值
    // 编辑状态下非1001权限下，先将原来权限对应的数组进行清空，然后再赋予新的与之对应的数组数据
    if (detailsAllParams.projectRequisition && detailsAllParams.projectRequisition.applyEmployee) {
      const oldApplyEmployee = detailsAllParams.projectRequisition.applyEmployee;
      if (String(oldApplyEmployee) !== '1001') {
        detailsAllParams[arrLabel[oldApplyEmployee]] = null;
        detailsAllParams[_arrLabel[oldApplyEmployee]] = null;
      }
    }
    const params = {
      startUseDate: moment(values.startUseDate),
      closeUseDate: values.closeUseDate ? moment(values.closeUseDate) : '',
      projectStartDate: values.projectStartDate ? moment(values.projectStartDate) : '',
      projectCloseDate: values.projectCloseDate ? moment(values.projectCloseDate) : '',
      applyEmployee: String(visibleUserScope), // 1001,1002,1003,1004
    };
    const { projectRequisition } = detailsAllParams;
    detailsAllParams.projectRequisition = { ...projectRequisition, ...params };

    detailsAllParams[arrLabel[visibleUserScope]] = [...ids];

    return detailsAllParams;
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
    const { saveLoading, permissions } = this.state;

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
            <Col>
              <FormItem labelCol={{ span: 6 }} wrapperCol={{ span: 18 }} label="适用权限">
                {getFieldDecorator('userOrCompanyOrDepList', {
                  initialValue: permissions,
                })(<PermissionsSetting />)}
              </FormItem>
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
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    company: state.user.company,
  };
}
export default connect(mapStateToProps)(Form.create()(CNewProjectDetails));
