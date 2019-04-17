import React, { Component } from 'react';
import { Form, Input, Row, Col, Button, Select, message, Spin, DatePicker, Radio } from 'antd';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import Chooser from 'widget/chooser';
import Upload from 'widget/upload';

import service from './service';
import config from 'config';

const FormItem = Form.Item;

import moment from 'moment';

class NewTravelApplicationFrom extends Component {
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
      companyId: props.user.companyId,
      dimensionValues: {},
      togetherBooking: true,
    };
  }

  componentWillMount() {
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
          res.data.travelPeopleDTOList.map(o => {
            (o.userId = o.employeeId), (o.userName = o.employeeName);
          });
          res.data.travelFromPlaceDTOS.map(o => {
            (o.id = o.placeId), (o.city = o.placeName);
          });
          res.data.travelToPlaceDTOS.map(o => {
            (o.id = o.placeId), (o.city = o.placeName);
          });
          this.setState({
            isNew: false,
            model: res.data,
            uploadOIDs: fileList.map(o => o.uid),
            fileList,
            pageLoading: false,
            companyId: res.data.companyId,
            togetherBooking: res.data.orderMode === '1',
            dimensionList: res.data.dimensions || [],
          });
        })
        .catch(err => {
          message.error(err.response.data.message);
        });
    }

    //获取币种列表
    service.getCurrencyList(this.props.company.companyOid).then(res => {
      this.setState({ currencyList: res.data });
    });

    if (this.props.match.params.typeId) {
      this.getApplicationTypeInfo(this.props.match.params.typeId);
    }
  }

  getApplicationTypeInfo = typeId => {
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

  //返回
  onBack = () => {
    if (!this.state.isNew) {
      this.props.dispatch(
        routerRedux.push({
          pathname: '/travel-application/travel-application-detail/' + this.props.match.params.id,
        })
      );
    } else {
      this.props.dispatch(
        routerRedux.push({
          pathname: '/travel-application/travel-application-form',
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

      let travelPeopleDTOList = values.travelPeople.map(o => ({ employeeId: o.userId }));
      let travelFromPlaceDTOS = values.travelFromPlace.map(o => ({ placeId: o.id }));
      let travelToPlaceDTOS = values.travelToPlace.map(o => ({ placeId: o.id }));
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
          documentTypeId: typeId,
          employeeId: this.props.user.id,
          description: values.description,
          currencyCode: values.currencyCode,
          companyId: values.company[0].id,
          unitId: values.department[0].departmentId,
          dimensions: dimensionList,
          attachmentOid: uploadOIDs.length ? uploadOIDs.join(',') : null,
          startDate: values.startDate,
          endDate: values.endDate,
          requisitionDate: moment().format(),
          orderMode: values.orderMode,
          orderer: values.orderer.length > 0 ? values.orderer[0].userId : null,
          travelPeopleDTOList: travelPeopleDTOList,
          travelFromPlaceDTOS: travelFromPlaceDTOS,
          travelToPlaceDTOS: travelToPlaceDTOS,
        };
      } else {
        values = {
          ...model,
          id: model.id,
          documentTypeId: model.documentTypeId,
          employeeId: model.employeeId,
          description: values.description,
          currencyCode: values.currencyCode,
          companyId: values.company[0].id,
          unitId: values.department[0].departmentId,
          dimensions: isNew ? dimensionList : dimensionList.filter(o => o.headerFlag),
          attachmentOid: uploadOIDs.length ? uploadOIDs.join(',') : null,
          requisitionDate: model.requisitionDate,
          startDate: values.startDate,
          endDate: values.endDate,
          versionNumber: model.versionNumber,
          orderMode: values.orderMode,
          orderer: values.orderer.length > 0 ? values.orderer[0].userId : null,
          travelPeopleDTOList: travelPeopleDTOList,
          travelFromPlaceDTOS: travelFromPlaceDTOS,
          travelToPlaceDTOS: travelToPlaceDTOS,
        };
      }
      let method = service.addTravelApplictionForm;
      if (!isNew) {
        method = service.updateHeaderData;
      }
      method(values)
        .then(res => {
          message.success('操作成功！');
          this.setState({ loading: false });
          this.props.dispatch(
            routerRedux.push({
              pathname: '/travel-application/travel-application-detail/' + res.data.id,
            })
          );
        })
        .catch(err => {
          message.error(err.response.data.message);
          this.setState({ loading: false });
        });
    });
  };

  companyChange = value => {
    this.companyOrUnitChange(value[0].id, 'oldId');
  };

  unitChange = value => {
    this.companyOrUnitChange('oldId', value[0].departmentId);
  };

  companyOrUnitChange = (companyId, unitId) => {
    let oldCompany = this.props.form.getFieldValue('company');
    let oldUnit = this.props.form.getFieldValue('department');
    if (companyId === 'oldId') {
      companyId = oldCompany[0].id;
    }
    if (unitId === 'oldId') {
      unitId = oldUnit[0].departmentId;
    }
    let user = this.props.user;
    this.setDimension(this.state.dimensionList, companyId, unitId, user.id);
  };

  getDimensionValues = (open, item) => {
    let { dimensionValues } = this.state;
    if (open && !dimensionValues[item.dimensionId]) {
      service
        .getDimensionValues(item.dimensionId, this.state.companyId)
        .then(res => {
          this.setState({ dimensionValues: { ...dimensionValues, [item.dimensionId]: res.data } });
        })
        .catch(err => {
          message.error(err.response.data.message);
        });
    }
  };

  radioChange = value => {
    this.props.form.setFieldsValue({
      orderer: [],
    });
    this.setState({
      togetherBooking: value.target.value === '1' ? true : false,
    });
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
      dimensionList,
      fileList,
      model,
      togetherBooking,
    } = this.state;

    return (
      <div className="new-contract" style={{ marginBottom: 60, marginTop: 10 }}>
        <Spin spinning={pageLoading}>
          {!pageLoading && (
            <Form onSubmit={this.handleSave}>
              <Row {...rowLayout}>
                <Col span={10}>
                  <FormItem label="申请人" {...formItemLayout}>
                    {getFieldDecorator('user', {
                      rules: [{ required: true, message: '请选择' }],
                      initialValue: isNew ? this.props.user.userName : model.employeeName,
                    })(<Input disabled />)}
                  </FormItem>
                </Col>
              </Row>
              <Row {...rowLayout}>
                <Col span={10}>
                  <FormItem label="公司" {...formItemLayout}>
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
                  <FormItem label="部门" {...formItemLayout}>
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
                              departmentId: model.unitId,
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
                  <FormItem label="币种" {...formItemLayout}>
                    {getFieldDecorator('currencyCode', {
                      rules: [{ required: true, message: this.$t('common.please.select') }],
                      initialValue: isNew ? this.props.company.baseCurrency : model.currencyCode,
                    })(
                      <Select disabled={!isNew}>
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
              <Row {...rowLayout}>
                <Col span={10}>
                  <FormItem label="开始日期" {...formItemLayout}>
                    {getFieldDecorator('startDate', {
                      rules: [{ required: true, message: this.$t('common.please.select') }],
                      initialValue: isNew ? '' : moment(model.startDate),
                    })(<DatePicker style={{ width: '100%' }} />)}
                  </FormItem>
                </Col>
              </Row>
              <Row {...rowLayout}>
                <Col span={10}>
                  <FormItem label="结束日期" {...formItemLayout}>
                    {getFieldDecorator('endDate', {
                      rules: [{ required: true, message: this.$t('common.please.select') }],
                      initialValue: isNew ? '' : moment(model.endDate),
                    })(<DatePicker style={{ width: '100%' }} />)}
                  </FormItem>
                </Col>
              </Row>
              <Row {...rowLayout}>
                <Col span={10}>
                  <FormItem label="出发地" {...formItemLayout}>
                    {getFieldDecorator('travelFromPlace', {
                      rules: [{ required: true, message: this.$t('common.please.select') }],
                      initialValue: isNew ? [] : model.travelFromPlaceDTOS,
                    })(
                      // <Chooser
                      //   type="company"
                      //   labelKey="name"
                      //   valueKey="id"
                      //   listExtraParams={{ setOfBooksId: this.props.company.setOfBooksId }}
                      //   showClear={false}
                      // />
                      <Chooser
                        type="select_city"
                        labelKey="city"
                        valueKey="id"
                        listExtraParams={{ code: 'CHN000000000' }}
                        showClear={false}
                      />
                    )}
                  </FormItem>
                </Col>
              </Row>
              <Row {...rowLayout}>
                <Col span={10}>
                  <FormItem label="目的地" {...formItemLayout}>
                    {getFieldDecorator('travelToPlace', {
                      rules: [{ required: true, message: this.$t('common.please.select') }],
                      initialValue: isNew ? [] : model.travelToPlaceDTOS,
                    })(
                      // <Chooser
                      //   type="company"
                      //   labelKey="name"
                      //   valueKey="id"
                      //   listExtraParams={{ setOfBooksId: this.props.company.setOfBooksId }}
                      //   showClear={false}
                      // />
                      <Chooser
                        type="select_city"
                        labelKey="city"
                        valueKey="id"
                        listExtraParams={{ code: 'CHN000000000' }}
                        showClear={false}
                      />
                    )}
                  </FormItem>
                </Col>
              </Row>
              <Row {...rowLayout}>
                <Col span={10}>
                  <FormItem label="出行人员" {...formItemLayout}>
                    {getFieldDecorator('travelPeople', {
                      rules: [{ required: true, message: this.$t('common.please.select') }],
                      initialValue: isNew ? [] : model.travelPeopleDTOList,
                    })(
                      <Chooser
                        type="select_authorization_user"
                        labelKey="userName"
                        valueKey="userId"
                        listExtraParams={{ setOfBooksId: this.props.company.setOfBooksId }}
                        showClear={false}
                      />
                    )}
                  </FormItem>
                </Col>
              </Row>
              <Row {...rowLayout}>
                <Col span={10}>
                  <FormItem label="订票模式" {...formItemLayout}>
                    {getFieldDecorator('orderMode', {
                      initialValue: isNew ? '1' : model.orderMode,
                    })(
                      <Radio.Group onChange={value => this.radioChange(value)}>
                        <Radio value="1">统一订票</Radio>
                        <Radio value="2">出行人分别订票</Radio>
                      </Radio.Group>
                    )}
                  </FormItem>
                </Col>
              </Row>
              <Row {...rowLayout}>
                <Col span={10}>
                  <FormItem label="订票人" {...formItemLayout}>
                    {getFieldDecorator('orderer', {
                      rules: [
                        { required: togetherBooking, message: this.$t('common.please.select') },
                      ],
                      initialValue: isNew
                        ? []
                        : model.orderer
                          ? [{ userId: model.orderer, userName: model.orderName }]
                          : [],
                    })(
                      <Chooser
                        type="select_authorization_user"
                        labelKey="userName"
                        valueKey="userId"
                        single
                        disabled={!togetherBooking}
                        listExtraParams={{ setOfBooksId: this.props.company.setOfBooksId }}
                        showClear={false}
                      />
                    )}
                  </FormItem>
                </Col>
              </Row>
              <Row {...rowLayout}>
                <Col span={10}>
                  <FormItem label="备注" {...formItemLayout}>
                    {getFieldDecorator('description', {
                      rules: [{ required: true, message: this.$t('common.please.input') }],
                      initialValue: isNew ? '' : model.description,
                    })(<Input.TextArea autosize={{ minRows: 3 }} />)}
                  </FormItem>
                </Col>
              </Row>
              <Row {...rowLayout} style={{ marginBottom: 40 }}>
                <Col span={10}>
                  <FormItem label="附件信息" {...formItemLayout}>
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
                  {isNew ? '下一步' : '确定'}
                </Button>
                {isNew ? (
                  <Button onClick={this.onBack}>取消</Button>
                ) : (
                  <Button onClick={this.onBack}>返回</Button>
                )}
              </div>
            </Form>
          )}
        </Spin>
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

export default connect(map)(Form.create()(NewTravelApplicationFrom));
