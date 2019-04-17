import React, { Component } from 'react';
import { Form, Select, message, Input, Row, Col, Table, Popover, Button } from 'antd';
import service from './service';

const FormItem = Form.Item;

class EditLineFrame extends Component {
  constructor(props) {
    super(props);
    this.state = {
      transferTypeList: [],
      useTypeList: [],
      columns: [
        {
          title: this.$t('expense-report.association.request'),
          dataIndex: 'applicationNumber',
          align: 'center',
          render: (value, record) => {
            return (
              <Popover content={value}>
                <a onClick={() => this.showDocumentDetail(record)}>{value}</a>
              </Popover>
            );
          },
          width: 120,
        },
        {
          title: this.$t('acp.company'),
          dataIndex: 'companyName',
          align: 'center',
          render: value => {
            return (
              <Popover content={value}>
                <span>{value}</span>
              </Popover>
            );
          },
          width: 120,
        },
        {
          title: this.$t('common.department'),
          dataIndex: 'departmentName',
          align: 'center',
          render: value => {
            return (
              <Popover content={value}>
                <span>{value}</span>
              </Popover>
            );
          },
          width: 120,
        },
        {
          title: this.$t('expense.apportion.amount'),
          dataIndex: 'contributions',
          align: 'center',
          render: amount => {
            return <Popover content={amount}>{this.filterMoney(amount, 2)}</Popover>;
          },
          width: 120,
        },
        {
          title: this.$t('tax.share.tax'),
          dataIndex: 'shareTax',
          align: 'center',
          render: amount => {
            return <Popover content={amount}>{this.filterMoney(amount, 2)}</Popover>;
          },
          width: 120,
        },
      ],
      dataSources: [],
      selectedRowKeys: [], // 选中的行rowKey
      pagination: {
        showSizeChanger: true,
        showQuickJumper: true,
        showTotal: (total, range) =>
          this.$t('common.show.total', {
            range0: `${range[0]}`,
            range1: `${range[1]}`,
            total,
          }),
      },
      saveLoading: false,
    };
  }

  componentDidMount = () => {
    // 业务大类
    this.getSystemValueList('transferType')
      .then(res => {
        const transferTypeList = res.data.values;
        this.setState({ transferTypeList });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });

    // 用途类型
    this.getSystemValueList('useType')
      .then(res => {
        const useTypeList = res.data.values;
        this.setState({ useTypeList });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  // 多选
  handleGetShareTax = (selectedRowKeys, selectedRows) => {
    this.setState({
      selectedRowKeys,
    });
    console.log(selectedRows);
  };

  // 关联申请单
  showDocumentDetail = () => {};

  // 保存
  handleSubmit = e => {
    e.preventDefault();
    const { form } = this.props;
    const { validateFields } = form;
    this.setState({ saveLoading: true });
    validateFields((err, values) => {
      if (err) return;
      console.log(values);
      const params = { ...values };
      service
        .updateLineValue(params)
        .then(res => {
          if (res) {
            message.success(this.$t('common.update.success'));
            this.setState({ saveLoading: false }, () => {
              const { onClose } = this.props;
              onClose(true);
            });
          }
        })
        .catch(error => {
          message.error(error.response.data.message);
        });
    });
  };

  // 取消
  handleCancel = () => {
    const { onClose } = this.props;
    onClose();
  };

  // 分页
  tablePageChange = pagination => {
    const { page, size } = this.state;
    this.setState({
      page: pagination.current - 1 || page,
      size: pagination.pageSize || size,
    });
  };

  render() {
    const { form, params, type } = this.props;
    const { getFieldDecorator } = form;
    const formItemLayout = {
      labelCol: { span: 8 },
      wrapperCol: { span: 10 },
    };
    const {
      transferTypeList,
      useTypeList,
      columns,
      dataSources,
      selectedRowKeys,
      pagination,
      saveLoading,
    } = this.state;
    const rowSelection = {
      selectedRowKeys,
      onChange: this.handleGetShareTax,
    };
    return (
      <div>
        <Form>
          <FormItem {...formItemLayout} label={this.$t('tax.business.categories')}>
            {getFieldDecorator('transferProportion', {
              rules: [
                {
                  required: true,
                  message: this.$t({ id: 'common.please.select' }),
                },
              ],
              initialValue: params.transferProportion,
            })(
              <Select
                placeholder={this.$t('common.please.select')}
                labelInValue
                disabled
                getPopupContainer={triggerNode => triggerNode.parentNode}
              >
                {transferTypeList.map(item => {
                  return <Select.Option key={item.value}>{item.name}</Select.Option>;
                })}
              </Select>
            )}
          </FormItem>
          <FormItem {...formItemLayout} label={this.$t('tax.rate')}>
            {getFieldDecorator('transferType', {
              rules: [
                {
                  required: true,
                  message: this.$t({ id: 'common.please.enter' }),
                },
              ],
              initialValue: params.transferProportion,
            })(<Input placeholder={this.$t('common.please.enter')} disabled />)}
          </FormItem>
          <FormItem {...formItemLayout} label={this.$t('tax.use.type')}>
            {getFieldDecorator('useTypeName', {
              rules: [
                {
                  required: true,
                  message: this.$t({ id: 'common.please.enter' }),
                },
              ],
              initialValue: params.useTypeName,
            })(
              <Select
                placeholder={this.$t('common.please.select')}
                labelInValue
                getPopupContainer={triggerNode => triggerNode.parentNode}
              >
                {useTypeList.map(item => {
                  return <Select.Option key={item.value}>{item.name}</Select.Option>;
                })}
              </Select>
            )}
          </FormItem>
          <FormItem {...formItemLayout} label={this.$t('common.expense.type')}>
            {getFieldDecorator('expenseTypeName', {
              rules: [
                {
                  required: true,
                  message: this.$t({ id: 'common.please.enter' }),
                },
              ],
              initialValue: params.expenseTypeName,
            })(
              <Select
                placeholder={this.$t('common.please.select')}
                labelInValue
                disabled
                getPopupContainer={triggerNode => triggerNode.parentNode}
              >
                {useTypeList.map(item => {
                  return <Select.Option key={item.value}>{item.name}</Select.Option>;
                })}
              </Select>
            )}
          </FormItem>
          <FormItem
            {...formItemLayout}
            label={
              type === 'FOR_SALE' ? this.$t('tax.sale.amount') : this.$t('tax.turnOut.tax')
              // '视同销售金额' : '转出税额'
            }
          >
            <Row gutter={8}>
              <Col span={10}>
                {getFieldDecorator('currencyCode', {
                  rules: [
                    {
                      required: true,
                      message: this.$t({ id: 'common.please.enter' }),
                    },
                  ],
                  initialValue: params.currencyCode,
                })(<Input disabled />)}
              </Col>
              <Col span={14}>
                {getFieldDecorator('baseAmount', {
                  rules: [
                    {
                      required: true,
                      message: this.$t({ id: 'common.please.enter' }),
                    },
                  ],
                  initialValue: params.baseAmount ? Number(params.baseAmount).toFixed(2) : 0,
                })(<Input disabled />)}
              </Col>
            </Row>
          </FormItem>
          <FormItem
            {...formItemLayout}
            label={
              type === 'FOR_SALE' ? this.$t('tax.as.sale.tax') : this.$t('tax.transfer.amount')
              // '视同销售税额' : '转出金额'
            }
          >
            <Row gutter={8}>
              <Col span={10}>
                {getFieldDecorator('currencyCode', {
                  rules: [
                    {
                      required: true,
                      message: this.$t({ id: 'common.please.enter' }),
                    },
                  ],
                  initialValue: params.currencyCode,
                })(<Input disabled />)}
              </Col>
              <Col span={14}>
                {getFieldDecorator('amount', {
                  rules: [
                    {
                      required: true,
                      message: this.$t({ id: 'common.please.enter' }),
                    },
                  ],
                  initialValue: params.amount ? Number(params.amount).toFixed(2) : 0,
                })(<Input disabled />)}
              </Col>
            </Row>
          </FormItem>
          <FormItem {...formItemLayout} label={this.$t('tax.msg.leave')}>
            {getFieldDecorator('description', {
              initialValue: params.description,
            })(
              <Input.TextArea
                placeholder={this.$t('tax.msg.leave.please')}
                autosize={{ minRows: 3 }}
              />
            )}
          </FormItem>
        </Form>
        <div className="common-item-title">{this.$t('tax.share.info')}</div>
        <Table
          columns={columns}
          dataSource={dataSources}
          style={{ margin: '10px 0 30px 0' }}
          scroll={{ x: 600 }}
          rowSelection={rowSelection}
          rowKey={record => record.id}
          pagination={pagination}
          onChange={this.tablePageChange}
        />
        <div className="slide-footer">
          <Button
            type="primary"
            htmlType="submit"
            loading={saveLoading}
            onClick={this.handleSubmit}
          >
            {this.$t('common.save')}
          </Button>
          <Button onClick={this.handleCancel}>{this.$t('common.cancel')}</Button>
        </div>
      </div>
    );
  }
}

export default Form.create()(EditLineFrame);
