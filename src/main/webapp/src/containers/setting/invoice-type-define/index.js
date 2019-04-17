import React, { Component } from 'react';
import SearchArea from 'widget/search-area';
import Table from 'widget/table';
import SlideFrame from 'widget/slide-frame';
import { Badge, message, Button, Modal, Alert, Row, Col, Popover } from 'antd';

import service from './invoice-type-define-service.js';
import NewInvoiceForm from './new-invoice-type-define.js';
import InvoiceTemplate from './invoice-template.js';

class InvoiceTypeDefine extends Component {
  constructor(props) {
    super(props);
    this.state = {
      searchForm: [
        {
          type: 'input',
          label: this.$t('expense.invoice.type.code') /*发票类型代码*/,
          id: 'invoiceTypeCode',
          placeholder: this.$t('expense.please.enter.the') /*请输入*/,
          colSpan: 6,
        },
        {
          type: 'input',
          label: this.$t('expense.invoice.type.name') /*发票类型名称*/,
          id: 'invoiceTypeName',
          placeholder: this.$t('expense.please.enter.the') /*请输入*/,
          colSpan: 6,
          language: true,
        },
        {
          type: 'value_list',
          label: this.$t('expense.whether.the.deduction') /*是否抵扣*/,
          id: 'deductionFlag',
          placeholder: this.$t('expense.please.select.a') /*请选择*/,
          colSpan: 6,
          options: [
            { value: 'N', label: this.$t('expense.no') } /*否*/,
            { value: 'Y', label: this.$t('expense.yes') } /*是*/,
          ],
          valueKey: 'value',
          labelKey: 'label',
        },
        {
          type: 'value_list',
          id: 'enabled',
          label: this.$t('expense.policy.enabled') /*状态*/,
          colSpan: '6',
          options: [
            { value: false, label: this.$t('expense.disable') } /*禁用*/,
            { value: true, label: this.$t('expense.enable') } /*启用*/,
          ],
          valueKey: 'value',
          labelKey: 'label',
        },
        {
          type: 'select',
          options: [],
          placeholder: this.$t('expense.please.select.a') /*请选择*/,
          id: 'setOfBooksId',
          label: this.$t('expense.policy.setofbooksname') /*账套*/,
          colSpan: 6,
          valueKey: 'value',
          labelKey: 'label',
        },
        {
          type: 'input',
          label: this.$t('expense.the.interface.map.values') /*接口映射值*/,
          id: 'interfaceMapping',
          placeholder: this.$t('expense.please.enter.the') /*请输入*/,
          colSpan: 6,
        },
      ],
      columns: [
        {
          title: this.$t('expense.invoice.type.code'),
          dataIndex: 'invoiceTypeCode',
          width: 150,
        } /*发票类型代码*/,
        {
          title: this.$t('expense.invoice.type.name'),
          dataIndex: 'invoiceTypeName',
          width: 150,
        } /*发票类型名称*/,
        {
          title: this.$t('expense.whether.the.deduction'),
          dataIndex: 'deductionFlag',
          width: 150 /*是否抵扣*/,
          render: (isDeduction, value, index) => {
            return (
              <span>
                {isDeduction.toUpperCase() !== 'N'
                  ? this.$t('expense.deduction')
                  : this.$t('expense.non.deduction')}
              </span>
            );
          },
        },
        {
          title: this.$t('expense.create.a.way'),
          dataIndex: 'creationMethod',
          width: 150 /*创建方式*/,
          render: (text, value, index) => {
            return (
              <span>
                {text.toUpperCase() == 'CUSTOM'
                  ? this.$t('expense.the.custom')
                  : this.$t('expense.system.preset')}
              </span>
            );
          },
        },
        {
          title: this.$t('expense.policy.setofbooksname'),
          dataIndex: 'setOfBooksName',
          width: 150 /*账套*/,
          render: (text, record, index) => {
            return record.setOfBooksName && record.setOfBooksCode ? (
              <span>
                <Popover
                  content={`${record.setOfBooksCode}-${record.setOfBooksName}`}
                  placement="leftTop"
                >
                  {`${record.setOfBooksCode}-${record.setOfBooksName}`}
                </Popover>
              </span>
            ) : (
              `-`
            );
          },
        },
        {
          title: this.$t('expense.invoice.code.digit'),
          dataIndex: 'invoiceCodeLength',
          width: 150,
          render: value => <div style={{ textAlign: 'right' }}>{value}</div>,
        } /*发票代码位数*/,
        {
          title: this.$t('expense.invoice.number.digits'),
          dataIndex: 'invoiceNumberLength',
          width: 150,
          render: value => <div style={{ textAlign: 'right' }}>{value}</div>,
        } /*发票号码位数*/,
        {
          title: this.$t('expense.the.default.rate'),
          dataIndex: 'defaultTaxRate',
          width: 150,
          render: value => <div style={{ textAlign: 'right' }}>{value}</div>,
        } /*默认税率*/,
        {
          title: this.$t('expense.the.interface.map.values'),
          dataIndex: 'interfaceMapping',
          width: 150,
          render: value => <div style={{ textAlign: 'right' }}>{value}</div>,
        } /*接口映射值*/,
        {
          title: this.$t('expense.policy.enabled') /*状态*/,
          dataIndex: 'enabled',
          width: 100,
          render: (enabled, record, index) => {
            return (
              <Badge
                status={enabled ? 'success' : 'error'}
                text={enabled ? this.$t('expense.enable') : this.$t('expense.disable')}
              />
            );
          },
        },
        {
          title: this.$t('expense.the.invoice.template.definition'),
          dataIndex: 'modelDefine',
          fixed: 'right',
          width: 150 /*发票模板定义*/,
          render: (text, record, index) => {
            return (
              <a onClick={e => this.handleViewModel(e, record)}>
                {this.$t('expense.the.invoice.template.definition')}
              </a>
            );
          },
        },
        {
          title: this.$t('expense.operation'),
          dataIndex: 'operation',
          fixed: 'right',
          width: 120 /*操作*/,
          render: (operation, record, index) => {
            return (
              <div>
                <a onClick={e => this.handleEditValue(e, record)}>
                  {' '}
                  {this.$t('expense.knitting.series')}
                </a>
                {/*编辑*/}
              </div>
            );
          },
        },
      ],
      loading: false,
      dataSources: [], //表格数据源
      searchConditions: {}, //搜索条件
      pagination: {
        showSizeChanger: true,
        showQuickJumper: true,
        showTotal: (total, range) =>
          this.$t('common.show.total', {
            range0: `${range[0]}`,
            range1: `${range[1]}`,
            total: total,
          }),
      }, //分页
      page: 0,
      size: 10,
      // selectedRowKeys: [], //多选框
      showSlideFrame: false, //是否显示新增、编辑模态框
      modelForm: {}, //模态框数据
      modalVisible: false, //是否显示发票模板
      templateTitle: '', //用于判断模板的标题及默认模板数据
      templateData: {},
      templateId: '', //模板新增时所需的id(发票类型id)
    };
  }

  componentDidMount = () => {
    this.getAllSetOfBooksValue();
    this.getInvoiceValueLists();
  };

  //搜索框搜索
  handleToSearch = params => {
    this.setState(
      {
        searchConditions: { ...params },
      },
      () => {
        this.getInvoiceValueLists();
      }
    );
  };

  //重置搜索
  handleClearSearch = () => {
    this.setState(
      {
        searchConditions: {},
      },
      () => {
        this.getInvoiceValueLists();
      }
    );
  };

  //获取账套信息-全部
  getAllSetOfBooksValue = () => {
    // baseService.getSetOfBooksByTenant()
    service
      .getSetOfBooksValue()
      .then(res => {
        let list = [];
        res.data.map(item => {
          list.push({
            value: item.setOfBooksId,
            label: `${item.setOfBooksCode}-${item.setOfBooksName}`,
          });
        });
        let form = this.state.searchForm;

        if (JSON.stringify(list) !== '[]') {
          form[4].options = list;
          this.setState({ searchForm: form });
        }
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  //获取表格数据源
  getInvoiceValueLists = () => {
    let { page, size, pagination, searchConditions } = this.state;
    this.setState({ loading: true });

    service
      .getInvoiceValues({ ...searchConditions, page, size })
      .then(res => {
        pagination.total = Number(res.headers['x-total-count']);
        this.setState({ dataSources: res.data, pagination, loading: false });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  //分页
  tablePageChange = pagination => {
    this.setState(
      {
        page: pagination.current - 1,
        size: pagination.pageSize || 10,
      },
      () => {
        this.getInvoiceValueLists();
      }
    );
  };

  //新增-显示模态框
  handleOpenForm = () => {
    this.setState({ showSlideFrame: true });
  };

  //编辑-显示模态框
  handleEditValue = (e, record) => {
    e.preventDefault();
    this.setState({
      modelForm: { ...record },
      showSlideFrame: true,
    });
  };

  //关闭模态框
  closeSlideForm = flag => {
    this.setState({ showSlideFrame: false, modelForm: {} }, () => {
      if (flag) {
        this.getInvoiceValueLists();
      }
    });
  };

  //发票模板
  handleViewModel = (e, record) => {
    e.preventDefault();
    service
      .getInvoiceTemplate(record.id)
      .then(res => {
        let data = this.initTemplateValue(res.data, record.deductionFlag);
        this.setState({
          modalVisible: true,
          templateTitle: record.deductionFlag,
          templateData: JSON.parse(JSON.stringify(data)),
          templateId: record.id,
        });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  };

  //初始化发票模板内数据
  initTemplateValue = (values, templateTitle) => {
    let templateData = {
      invoiceTypeMouldHeadColumn: {
        invoiceDate: ['ENABLED', 'REQUIRED'],
        invoiceCode: ['ENABLED', 'REQUIRED'],
        invoiceNo: ['ENABLED', 'REQUIRED'],
        currencyCode: ['ENABLED', 'REQUIRED'],
      },
      invoiceTypeMouldLineColumn: {
        detailAmount: ['ENABLED', 'REQUIRED'],
      },
    };
    //初始时，接口返回的values head与line 属性为null,将赋予disabled这一默认初始值
    if (!values.invoiceTypeMouldHeadColumn && !values.invoiceTypeMouldLineColumn) {
      let head = [
        'checkCode',
        'machineNo',
        'totalAmount',
        'invoiceAmount',
        'taxTotalAmount',
        'remark',
        'buyerName',
        'buyerTaxNo',
        'buyerAddPh',
        'buyerAccount',
        'salerName',
        'salerTaxNo',
        'salerAddPh',
        'salerAccount',
      ];
      let line = [
        'goodsName',
        'specificationModel',
        'unit',
        'num',
        'unitPrice',
        'taxRate',
        'taxAmount',
      ];

      head.forEach(item => {
        templateData.invoiceTypeMouldHeadColumn[item] = ['DISABLED'];
      });
      line.forEach(keys => {
        templateData.invoiceTypeMouldLineColumn[keys] = ['DISABLED'];
      });
      //根据模板类型不同设置不同的初始值
      if (templateTitle.toUpperCase() == 'Y') {
        templateData.invoiceTypeMouldLineColumn.taxRate = ['ENABLED', 'REQUIRED'];
        templateData.invoiceTypeMouldLineColumn.taxAmount = ['ENABLED', 'REQUIRED'];
      } else if (templateTitle.toUpperCase() == 'N') {
        templateData.invoiceTypeMouldHeadColumn.checkCode = ['ENABLED', 'REQUIRED'];
      }
    } else {
      //遍历获得接口返回的值values,将字符串转数组,并保留id,过滤多余字段
      for (let key in values) {
        for (let item in values[key]) {
          if (/^([A-Z]+)$/g.test(values[key][item])) {
            //过滤接口返回值中与模板字段不同的的部分
            templateData[key][item] =
              values[key][item] == 'ENABLED'
                ? ['ENABLED']
                : values[key][item] == 'REQUIRED'
                  ? ['REQUIRED', 'ENABLED']
                  : ['DISABLED'];
          } else if (item == 'id') {
            //保留headId与lineId以便修改
            templateData[key].id = values[key].id;
          }
        }
      }
    }
    return templateData;
  };

  //关闭模板模态框
  handleTemplateCancel = () => {
    this.setState({
      modalVisible: false,
      templateTitle: '',
      templateData: {},
      templateId: '',
    });
  };

  render() {
    const {
      searchForm,
      columns,
      loading,
      dataSources,
      pagination,
      modelForm,
      showSlideFrame,
      modalVisible,
      templateData,
      templateTitle,
      templateId,
    } = this.state;

    return (
      <div className="invoice-type-definition">
        <SearchArea
          maxLength={4}
          searchForm={searchForm}
          submitHandle={this.handleToSearch}
          clearHandle={this.handleClearSearch}
        />
        <div>
          <Button onClick={this.handleOpenForm} style={{ marginBottom: '20px' }} type="primary">
            {this.$t('common.create')}
          </Button>
        </div>
        <Table
          columns={columns}
          rowKey={record => record.id}
          size="middle"
          bordered
          dataSource={dataSources}
          loading={loading}
          pagination={pagination}
          onChange={this.tablePageChange}
          scroll={{ x: 1400 }}
        />
        <SlideFrame
          title={
            modelForm.id
              ? this.$t('expense.edit.the.invoice.type')
              : this.$t('expense.new.invoice.type')
          } /*新增发票类型*/ /*编辑发票类型*/
          show={showSlideFrame}
          onClose={() => {
            this.closeSlideForm(false);
          }}
        >
          <NewInvoiceForm params={modelForm} close={this.closeSlideForm} />
        </SlideFrame>
        {/*发票模板*/}
        <Modal
          visible={modalVisible}
          title={
            <div>
              <Row>
                <Col span={4}>
                  <span style={{ fontWeight: '700', fontSize: '20px', lineHeight: '40px' }}>
                    {templateTitle && templateTitle == 'N'
                      ? this.$t('expense.the.buckle.type.of.invoice')
                      : this.$t('expense.non.deductible.invoice')}
                  </span>
                </Col>
                <Col span={18}>
                  <Alert
                    type="info"
                    showIcon
                    message={this.$t('expense.desc.code1')} // 发票模板将影响手工填写此类发票时的发票样式。扫描发票及电子票夹导入 发票样式系统自动展示。
                  />
                </Col>
              </Row>
            </div>
          }
          width={880}
          style={{ padding: '10px' }}
          onCancel={this.handleTemplateCancel}
          footer={null}
        >
          <InvoiceTemplate
            templateTitle={templateTitle}
            params={templateData}
            templateId={templateId}
            onClose={this.handleTemplateCancel}
          />
        </Modal>
      </div>
    );
  }
}

export default InvoiceTypeDefine;
