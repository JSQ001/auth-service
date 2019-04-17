import { messages } from 'utils/utils';
import { deepCopy } from 'utils/extend';
import React from 'react';
import { connect } from 'dva';
import { Button, Form, Switch, Input, Row, Col, message, Spin, Select } from 'antd';
const Option = Select.Option;
const FormItem = Form.Item;
import defaultExpenseTypeIcon from 'images/expense/default-expense-type.png';
import 'styles/setting/expense-type/new-expense-type/expense-type-base.scss';
import baseService from 'share/base.service';
import IconSelector from 'containers/setting/expense-type/new-expense-type/icon-selector';
import expenseTypeService from 'containers/setting/expense-type/expense-type.service';
import { LanguageInput } from 'widget/index';
import PropTypes from 'prop-types';
import { routerRedux } from 'dva/router';
import CustomAmount from 'components/Widget/custom-amount';

class ExpenseTypeBase extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      showIconSelectorFlag: false,
      expenseTypeCategory: [],
      linkModel: [],
      linkAmount: [],
      name: '',
      amountDisabled: true,
      nameI18n: [],
      icon: {
        iconURL: '',
        iconName: '',
      },
      apportionEnabled: false,
      valid: 0,
      subsidyType: 0,
      saving: false,
      priceUnit: '',
      entryMode: false,
      types: [],
      budgetItemName: '',
      travelTypeFlag: false,
      travelTypeList: [],
      // expenseTypePage: menuRoute.getRouteItem('expense-type'),
      // expenseTypeDetailPage: menuRoute.getRouteItem('expense-type-detail')
    };
  }

  componentDidMount() {
    if (!this.props.expenseType) {
      if (!this.props.expenseTypeSetOfBooks.id) {
        this.goBack();
      } else {
        expenseTypeService.getExpenseTypeCategory(this.props.expenseTypeSetOfBooks.id).then(res => {
          this.setState({ expenseTypeCategory: res.data });
        });
      }
    } else {
      expenseTypeService.getExpenseTypeCategory(this.props.expenseType.setOfBooksId).then(res => {
        this.setState({
          expenseTypeCategory: res.data,
          budgetItemName: this.props.expenseType.budgetItemName,
          travelTypeFlag: !!this.props.expenseType.travelTypeCode,
        });
        this.setFieldsByExpenseType(this.props);
      });

      this.typeCategoryChange(this.props.expenseType.typeCategoryId);
    }
    expenseTypeService.getTravelTypes().then(res => {
      this.setState({
        travelTypeList: res.data,
      });
    });
  }

  goBack = () => {
    this.props.dispatch(
      routerRedux.push({
        pathname: '/admin-setting/expense-type/expense-type',
      })
    );

    // this.context.router.push(this.state.expenseTypePage.url)
  };

  setFieldsByExpenseType = props => {
    const { expenseType } = props;
    let valueWillSet = this.props.form.getFieldsValue();
    Object.keys(valueWillSet).map(key => {
      valueWillSet[key] = expenseType[key];
      !valueWillSet[key] && delete valueWillSet[key];
    });
    this.setState(
      {
        icon: {
          iconURL: expenseType.iconUrl,
          iconName: expenseType.iconName,
        },
        nameI18n: expenseType.i18n.name,
        name: expenseType.name,
        apportionEnabled: expenseType.apportionEnabled,
        valid: Number(expenseType.valid),
        subsidyType: expenseType.subsidyType,
        entryMode: expenseType.entryMode,
        budgetItemName: expenseType.budgetItemName,
        priceUnit: expenseType.priceUnit,
        attachmentFlag: expenseType.attachmentFlag + '',
      },
      () => {
        valueWillSet.pasteInvoiceNeeded = Number(valueWillSet.pasteInvoiceNeeded);
        valueWillSet.valid && (valueWillSet.valid = Number(valueWillSet.valid));
        valueWillSet.attachmentFlag = expenseType.attachmentFlag + '';
        valueWillSet.sourceTypeId &&
          (valueWillSet.sourceTypeId = {
            label: expenseType.sourceTypeName,
            key: valueWillSet.sourceTypeId,
          });
        valueWillSet.applicationModel &&
          (valueWillSet.applicationModel = {
            label: expenseType.applicationModelName,
            key: valueWillSet.applicationModel,
          });
        valueWillSet.attachmentFlag === 'null' && (valueWillSet.attachmentFlag = '');
        this.props.form.setFieldsValue(valueWillSet);
      }
    );
  };

  handleSave = e => {
    e.preventDefault();
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        const { icon, nameI18n, expenseTypeDetailPage } = this.state;
        if (icon.iconURL === '') {
          message.error(messages('expense.type.please.select.icon'));
          return;
        }
        values.typeFlag = 1;
        values.contrastSign = values.contrastSign && values.contrastSign.label;
        values.sourceTypeId = values.sourceTypeId && values.sourceTypeId.key;
        values.priceUnit = this.state.priceUnit;
        values.entryMode = this.state.entryMode;
        values.setOfBooksId = this.props.expenseTypeSetOfBooks.id;
        values.iconUrl = icon.iconURL;
        values.iconName = icon.iconName;
        values.applicationModel = values.applicationModel.key;
        if (this.props.expenseType) {
          values.id = this.props.expenseType.id;
          this.setState({ saving: true });
          expenseTypeService
            .editExpenseType(values)
            .then(res => {
              this.setState({ saving: false });
              this.props.onSave();
              message.success('更新成功！');
            })
            .catch(error => {
              this.setState({ saving: false });
              message.error(error.response.data.message);
            });
        } else {
          this.setState({ saving: true });
          expenseTypeService
            .saveExpenseType(values)
            .then(res => {
              this.setState({ saving: false });
              message.success(this.$t('common.save.success', { name: '' }));
              this.props.dispatch(
                routerRedux.push({
                  pathname: '/admin-setting/expense-type/expense-type-detail/' + res.data.id,
                })
              );
            })
            .catch(error => {
              this.setState({ saving: false });
              message.error(error.response.data.message);
            });
        }
      }
    });
  };

  handleSelectIcon = target => {
    const { icon } = this.state;
    icon.iconURL = target.iconURL;
    icon.iconName = target.iconName;
    this.setState({ showIconSelectorFlag: false, icon });
  };

  handleChangeI18n = (name, nameI18n) => {
    this.setState({ name, nameI18n });
  };

  entryModeChange = value => {
    if (value) {
      this.setState({ priceUnit: 'day', entryMode: value });
    } else {
      this.setState({ priceUnit: '', entryMode: value });
    }
  };

  typeCategoryChange = value => {
    this.props.form.getFieldValue('travelTypeCode');
    this.props.form.setFieldsValue({ sourceTypeId: '', travelTypeCode: '' });
    expenseTypeService
      .getTypes(value)
      .then(res => {
        this.setState({
          types: res.data,
          sourceTypeId: '',
        });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });

    let travelTypeFlag;
    this.state.expenseTypeCategory.map(item => {
      if (value === item.id) {
        travelTypeFlag = item.travelTypeFlag;
      }
    });
    this.setState({
      travelTypeFlag: travelTypeFlag,
    });
  };

  sourceTypeChange = value => {
    if (value) {
      let model = this.state.types.find(o => o.id == value.key);

      this.setState({
        budgetItemName: model.budgetItemName,
      });
    }
  };

  //获取关联申请单
  handleLinkModel = () => {
    !this.state.linkModel.length &&
      this.getSystemValueList('APPLICATION_MODEL').then(res => {
        this.setState({
          linkModel: res.data.values,
        });
      });
  };

  handleLinkModelChange = value => {
    this.setState({
      // amountDisabled: !(value.key === '1096328818323800065'),
      amountDisabled: !(value.key === 'MUST'),
    });
    this.props.form.setFieldsValue({
      contrastSign: '',
      amount: '',
    });
  };

  handleAmountOpt = () => {
    !this.state.linkAmount.length &&
      this.getSystemValueList(2212).then(res => {
        console.log(res);
        let arr = [res.data.values[0], res.data.values[4]];
        this.setState({
          linkAmount: arr,
        });
      });
  };

  changeTravelType = () => {};

  render() {
    const { getFieldDecorator } = this.props.form;
    const {
      icon,
      showIconSelectorFlag,
      amountDisabled,
      expenseTypeCategory,
      apportionEnabled,
      valid,
      saving,
      name,
      nameI18n,
      subsidyType,
      types,
      linkModel,
      linkAmount,
      travelTypeFlag,
      travelTypeList,
    } = this.state;
    const formItemLayout = {
      labelCol: { span: 4 },
      wrapperCol: { span: 8, offset: 1 },
    };
    const { expenseType, expenseTypeSetOfBooks, tenantMode } = this.props;
    return (
      <Form className="expense-type-base" onSubmit={this.handleSave}>
        <FormItem {...formItemLayout} label={messages('setting.set.of.book')} required>
          <Input disabled value={expenseTypeSetOfBooks.setOfBooksName} />
        </FormItem>
        <FormItem {...formItemLayout} label={messages('图标')} required>
          <img
            src={icon.iconURL || defaultExpenseTypeIcon}
            className="expense-type-icon"
            onClick={() => {
              tenantMode && this.setState({ showIconSelectorFlag: true });
            }}
          />
        </FormItem>
        <FormItem {...formItemLayout} label={messages('费用类型代码')}>
          {getFieldDecorator('code', {
            rules: [
              {
                required: true,
                message: messages('common.please.enter'),
              },
            ],
          })(<Input disabled={!!expenseType} />)}
        </FormItem>
        <FormItem {...formItemLayout} label={messages('费用类型名称')}>
          {getFieldDecorator('name', {
            rules: [
              {
                required: true,
                message: messages('common.please.enter'),
              },
            ],
          })(
            <LanguageInput
              name={name}
              i18nName={nameI18n}
              nameChange={this.handleChangeI18n}
              isEdit={!!expenseType}
              inpRule={[
                {
                  length: 30,
                  language: 'zh_cn',
                },
                {
                  length: 30,
                  language: 'en',
                },
              ]}
              disabled={!tenantMode}
            />
          )}
        </FormItem>
        <FormItem {...formItemLayout} label={messages('common.column.status')}>
          {getFieldDecorator('enabled', {
            valuePropName: 'checked',
            initialValue: true,
          })(<Switch />)}
        </FormItem>
        <FormItem {...formItemLayout} label={messages('分类名称')}>
          {getFieldDecorator('typeCategoryId', {
            rules: [
              {
                required: true,
                message: messages('common.please.select'),
              },
            ],
          })(
            <Select
              onChange={this.typeCategoryChange}
              style={{ width: '100%' }}
              disabled={!tenantMode}
            >
              {expenseTypeCategory.map(item => <Option key={item.id}>{item.name}</Option>)}
            </Select>
          )}
        </FormItem>
        {travelTypeFlag && (
          <FormItem {...formItemLayout} label="差旅类型">
            {getFieldDecorator('travelTypeCode', {
              rules: [
                {
                  required: true,
                  message: messages('common.please.select'),
                },
              ],
              initialValue: this.props.expenseType ? this.props.expenseType.travelTypeCode : '',
            })(
              <Select allowClear onChange={this.changeTravelType}>
                {travelTypeList.map(option => {
                  return (
                    <Select.Option key={option.value} value={option.value}>
                      {option.name}
                    </Select.Option>
                  );
                })}
              </Select>
            )}
          </FormItem>
        )}
        <FormItem {...formItemLayout} label={messages('申请类型')}>
          {getFieldDecorator('sourceTypeId', {})(
            <Select
              labelInValue={true}
              allowClear
              onChange={this.sourceTypeChange}
              disabled={!this.props.form.getFieldValue('typeCategoryId')}
              style={{ width: '100%' }}
            >
              {types.map(item => <Option key={item.id}>{item.name}</Option>)}
            </Select>
          )}
        </FormItem>
        <FormItem {...formItemLayout} label={messages('关联申请单模式')}>
          {getFieldDecorator('applicationModel', {
            rules: [
              {
                required: true,
                message: messages('common.please.select'),
              },
            ],
          })(
            <Select
              labelInValue={true}
              allowClear
              onFocus={this.handleLinkModel}
              onChange={this.handleLinkModelChange}
              style={{ width: '100%' }}
            >
              {linkModel.map(item => <Option key={item.value}>{item.name}</Option>)}
            </Select>
          )}
        </FormItem>
        <FormItem {...formItemLayout} label={messages('关联金额条件')}>
          <Row gutter={20}>
            <Col span={8}>
              {getFieldDecorator('contrastSign', {})(
                <Select
                  labelInValue={true}
                  onFocus={this.handleAmountOpt}
                  disabled={amountDisabled}
                  // style={{ width: '38%' }}
                >
                  {linkAmount.map(item => <Option key={item.id}>{item.name}</Option>)}
                </Select>
              )}
            </Col>
            <Col span={16}>
              {getFieldDecorator('amount', {})(
                <CustomAmount
                  disabled={amountDisabled}
                  onFocus={this.handleAmountOpt}
                  //style={{ width: '58%', marginLeft: 18 }}
                >
                  {linkAmount.map(item => (
                    <Option value={item.id} key={item.id}>
                      {item.name}
                    </Option>
                  ))}
                </CustomAmount>
              )}
            </Col>
          </Row>
        </FormItem>
        {this.props.form.getFieldValue('sourceTypeId') && (
          <FormItem {...formItemLayout} label={messages('预算项目')}>
            <Input value={this.state.budgetItemName} disabled style={{ width: '100%' }} />
          </FormItem>
        )}
        <FormItem {...formItemLayout} label={messages('金额录入模式')}>
          {getFieldDecorator('entryMode', {
            initialValue: 0,
          })(
            <Row gutter={20}>
              <Col span={16}>
                <Select
                  value={this.state.entryMode}
                  allowClear
                  style={{ width: '100%' }}
                  onChange={this.entryModeChange}
                >
                  <Option value={false}>总金额</Option>
                  <Option value={true}>单价*数量</Option>
                </Select>
              </Col>
              <Col span={8}>
                <Select
                  value={this.state.entryMode ? this.state.priceUnit : ''}
                  style={{ width: '100%' }}
                  disabled={!this.state.entryMode}
                  onChange={value => this.setState({ priceUnit: value })}
                >
                  <Option value="day">天</Option>
                  <Option value="week">周</Option>
                  <Option value="month">月</Option>
                  <Option value="person">人</Option>
                  <Option value="ge">个</Option>
                  <Option value="time">次</Option>
                </Select>
              </Col>
            </Row>
          )}
        </FormItem>
        <FormItem {...formItemLayout} label={messages('附件')}>
          {getFieldDecorator('attachmentFlag', {})(
            <Select allowClear style={{ width: '100%' }}>
              <Option value="1">始终必填</Option>
              <Option value="2">始终不必填</Option>
              <Option value="3">仅有发票原件时不必填</Option>
              <Option value="4">有发票时不必填</Option>
            </Select>
          )}
        </FormItem>
        <FormItem {...formItemLayout} wrapperCol={{ offset: 5 }}>
          <Button type="primary" htmlType="submit" loading={saving}>
            {messages('common.save')}
          </Button>
        </FormItem>
        <IconSelector
          visible={showIconSelectorFlag}
          onOk={this.handleSelectIcon}
          onCancel={() => this.setState({ showIconSelectorFlag: false })}
        />
      </Form>
    );
  }
}

function mapStateToProps(state) {
  return {
    company: state.user.company,
    language: state.languages.languages,
    expenseTypeSetOfBooks: state.setting.expenseTypeSetOfBooks,
    tenantMode: true,
  };
}

ExpenseTypeBase.propTypes = {
  expenseType: PropTypes.object,
  onSave: PropTypes.func,
};

const WrappedExpenseTypeBase = Form.create()(ExpenseTypeBase);

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(WrappedExpenseTypeBase);
