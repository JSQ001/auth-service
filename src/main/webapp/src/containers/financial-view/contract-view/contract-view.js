/* eslint-disable */
import React from 'react';
import { connect } from 'dva';
import SearchArea from 'widget/search-area';
import { messages } from 'utils/utils';
import { Input, Button, Badge, message, Popover, Row, Col, Modal, Form } from 'antd';
import CustomTable from 'components/Widget/custom-table';
import config from 'config';
import ExcelExporter from 'widget/excel-exporter';
import conContractService from 'containers/contract/contract-approve/contract.service';
import moment from 'moment';
import FileSaver from 'file-saver';
import debounce from 'lodash.debounce';
import contractService from './contract-view.service';
import RelationInfo from './contract-relation-info';
import ContractDetail from 'containers/contract/contract-approve/contract-detail-common';

const { Search } = Input;

const statusList = [
  { value: 1001, label: messages('common.editing') },
  { value: 1002, label: messages('common.approving') },
  { value: 1003, label: messages('common.withdraw') },
  { value: 1004, label: messages('common.approve.pass') },
  { value: 1005, label: messages('common.approve.rejected') },
  { value: 6001, label: messages('my.contract.state.hold') },
  { value: 6002, label: messages('my.contract.state.cancel') },
  { value: 6003, label: messages('my.contract.state.finish') },
];

class ContractView extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      showContract: false,
      searchForm: [
        {
          type: 'list',
          colSpan: 6,
          id: 'companyId',
          label: this.$t({ id: 'my.contract.contractCompany' } /* 公司 */),
          listType: 'company',
          valueKey: 'id',
          labelKey: 'name',
          options: [],
          listExtraParams: { setOfBooksId: props.company.setOfBooksId },
          single: true,
        },
        {
          type: 'input',
          colSpan: 6,
          id: 'contractName',
          label: this.$t({ id: 'my.contract.name' } /* 合同名称 */),
        },
        {
          type: 'list',
          colSpan: 6,
          id: 'contractTypeId',
          label: this.$t({ id: 'my.contract.type' } /* 合同类型 */),
          single: true,
          labelKey: 'contractTypeName',
          valueKey: 'id',
          listType: 'contract_type',
          listExtraParams: { companyId: props.company.id },
        },
        {
          type: 'select',
          colSpan: 6,
          id: 'status',
          label: this.$t({ id: 'common.column.status' } /* 状态 */),
          options: statusList,
        },
        {
          type: 'list',
          id: 'createdBy',
          label: '申请人',
          colSpan: 6,
          listType: 'bgtUser',
          valueKey: 'id',
          labelKey: 'fullName',
          single: true,
        },
        {
          type: 'items',
          colSpan: 6,
          id: 'dateRange',
          items: [
            {
              type: 'date',
              id: 'signDateFrom',
              label: this.$t({ id: 'my.contract.signDate.from' } /* 签署日期从 */),
            },
            {
              type: 'date',
              id: 'signDateTo',
              label: this.$t({ id: 'my.contract.signDate.to' } /* 签署日期至 */),
            },
          ],
        },
        {
          type: 'value_list',
          colSpan: 6,
          id: 'partnerCategory',
          label: this.$t({ id: 'my.contract.partner.category' } /* 合同方类型 */),
          valueListCode: 2107,
          options: [],
          event: 'CON_PARTNER_TYPE',
        },
        {
          type: 'list',
          colSpan: 6,
          id: 'partnerId',
          label: this.$t({ id: 'my.contract.partner' } /* 合同方 */),
          single: true,
          valueKey: 'id',
          disabled: true,
          labelKey: 'name',
          listTitle: '选择合同方',
        },
        {
          type: 'items',
          colSpan: 6,
          id: 'amountRange',
          items: [
            {
              type: 'inputNumber',
              id: 'amountFrom',
              label: this.$t({ id: 'my.contract.amount.from' } /* 合同金额从 */),
            },
            {
              type: 'inputNumber',
              id: 'amountTo',
              label: this.$t({ id: 'my.contract.amount.to' } /* 合同金额至 */),
            },
          ],
        },
        {
          type: 'input',
          colSpan: 6,
          id: 'remark',
          label: this.$t({ id: 'common.comment' } /* 备注 */),
        },
      ],
      columns: [
        {
          title: '合同编号',
          dataIndex: 'contractNumber',
          width: 180,
          align: 'center',
          render: (contractNumber, record) => {
            return (
              <Popover content={contractNumber}>
                <a onClick={() => this.handleLink(record)}>{contractNumber}</a>
              </Popover>
            );
          },
        },
        {
          title: '合同名称',
          dataIndex: 'contractName',
          width: 100,
          align: 'center',
          render: desc => (
            <span>
              <Popover content={desc}>{desc || ''}</Popover>
            </span>
          ),
        },
        {
          title: '合同公司',
          dataIndex: 'companyName',
          width: 180,
          align: 'center',
          render: desc => (
            <span>
              <Popover content={desc}>{desc || ''}</Popover>
            </span>
          ),
        },
        {
          title: '申请人',
          dataIndex: 'created.fullName',
          width: 100,
          align: 'center',
          render: desc => (
            <span>
              <Popover content={desc}>{desc || ''}</Popover>
            </span>
          ),
        },
        {
          title: '签署日期',
          dataIndex: 'signDate',
          width: 160,
          align: 'center',
          render: desc => (
            <span>
              <Popover content={moment(desc).format('YYYY-MM-DD')}>
                {desc ? moment(desc).format('YYYY-MM-DD') : ''}
              </Popover>
            </span>
          ),
        },
        {
          title: '责任部门',
          dataIndex: 'unitName',
          width: 150,
          align: 'center',
          render: desc => (
            <span>
              <Popover content={desc}>{desc || ''}</Popover>
            </span>
          ),
        },
        {
          title: '责任人',
          dataIndex: 'employee.fullName',
          width: 100,
          align: 'center',
          render: desc => (
            <span>
              <Popover content={desc}>{desc || ''}</Popover>
            </span>
          ),
        },
        {
          title: '类型 合同方',
          dataIndex: 'partnerCategoryName',
          width: 150,
          align: 'center',
          render: (value, record) => {
            return value ? (
              <div>
                {value}
                <span className="ant-divider" />
                {record.partnerName}
              </div>
            ) : (
              '-'
            );
          },
        },
        {
          title: '币种',
          dataIndex: 'currency',
          width: 100,
          align: 'center',
          render: desc => (
            <span>
              <Popover content={desc}>{desc || ''}</Popover>
            </span>
          ),
        },
        {
          title: '金额',
          dataIndex: 'amount',
          width: 100,
          align: 'center',
          render: desc => (
            <span>
              <Popover content={this.filterMoney(desc, 2)}>{this.filterMoney(desc, 2)}</Popover>
            </span>
          ),
        },
        {
          title: '备注',
          dataIndex: 'remark',
          width: 200,
          align: 'center',
          render: desc => (
            <span>
              <Popover content={desc}>{desc || ''}</Popover>
            </span>
          ),
        },
        {
          title: '状态',
          dataIndex: 'status',
          width: 120,
          align: 'center',
          render: value => (
            <Badge status={this.$statusList[value].state} text={this.$statusList[value].label} />
          ),
        },
        {
          title: '查看信息',
          dataIndex: 'view',
          align: 'center',
          render: (view, record) => {
            return (
              <div>
                <a onClick={e => this.onRelateClick(record)}>关联信息</a>
              </div>
            );
          },
        },
      ],
      loading: true,
      pagination: {
        total: 0,
        showSizeChanger: true,
        showQuickJumper: true,
      },
      data: [],
      searchParams: {},
      headerData: undefined,
      showChild: false,
      page: 0,
      pageSize: 10,
      contractType: [],
      company: '',
      // 导出
      excelVisible: false,
      btLoading: false,
      exportColumns: [
        { title: '合同编号', dataIndex: 'contractNumber' },
        { title: '合同名称', dataIndex: 'contractName' },
        { title: '合同公司', dataIndex: 'companyName' },
        { title: '申请人', dataIndex: 'created.fullName' },
        { title: '签署日期', dataIndex: 'signDate' },
        { title: '开始日期', dataIndex: 'startDate' },
        { title: '结束日期', dataIndex: 'endDate' },
        { title: '责任部门', dataIndex: 'unitName' },
        { title: '责任人', dataIndex: 'employee.fullName' },
        { title: '类型 合同方', dataIndex: 'partnerCategoryName' },
        { title: '币种', dataIndex: 'currency' },
        { title: '金额', dataIndex: 'amount' },
        { title: '备注', dataIndex: 'remark' },
        { title: '状态', dataIndex: 'status' },
      ],
    };
    this.searchNumber = debounce(this.searchNumber, 500);
  }

  // 单据编号超链接
  handleLink = record => {
    this.setState({ contractHeaderId: record.id, showContract: true });
  };

  // 事件
  eventHandle = (type, value) => {
    const searchForm = this.state.searchForm;
    const { searchParams } = this.state;
    switch (type) {
      case 'CON_PARTNER_TYPE': {
        searchParams.partnerCategory = value;
        if (value) {
          searchForm[7].disabled = false;
          searchForm[7].listType = value === 'VENDER' ? 'select_ven' : 'select_payee_name_code';
          this.formRef.setValues({ partnerId: [] });
        } else {
          this.formRef.setValues({ partnerId: [] });
          searchForm[7].disabled = true;
        }
        break;
      }
    }
    this.setState({ searchParams, searchForm });
  };

  // 生命周期函数，constructor之后render之前
  componentWillMount = () => {
    this.getContractType();
  };

  getContractType = () => {
    const params = {
      companyId: this.props.company.id,
    };
    conContractService.getContractTypeByCompany(params).then(response => {
      this.setState({ contractType: response.data });
    });
  };

  // 点击重置的事件，清空值为初始值
  handleReset = () => {
    this.clearSearchAreaSelectData();
    const { searchParams } = this.state;
    this.props.clearHandle && this.props.clearHandle();
    this.setState({ searchParams: {} });
    this.eventHandle('id', null);
    this.eventHandle('code', null);
  };

  // 清除searchArea选择数据
  clearSearchAreaSelectData = () => {
    this.props.form.resetFields();
    this.state.checkboxListForm &&
      this.state.checkboxListForm.map(list => {
        if (!list.single) {
          list.items.map(item => {
            item.checked = [];
          });
        }
      });
  };

  change = e => {
    const { searchParams } = this.state;
    if (e && e.target && e.target.value) {
      searchParams.contractNumber = e.target.value;
    } else {
      searchParams.contractNumber = '';
    }
    this.setState({ searchParams });
  };

  // 关联信息
  onRelateClick = record => {
    this.setState({
      showChild: true,
      headerData: record,
    });
  };

  // 根据合同编号查询
  searchNumber = e => {
    this.setState(
      {
        searchParams: { ...this.state.searchParams, contractNumber: e },
      },
      () => {
        this.customTable.search({ ...this.state.searchParams, contractNumber: e });
      }
    );
  };

  // 搜索
  search = values => {
    values.signDateFrom && (values.signDateFrom = moment(values.signDateFrom).format('YYYY-MM-DD'));
    values.signDateTo && (values.signDateTo = moment(values.signDateTo).format('YYYY-MM-DD'));
    if (values.companyId && values.companyId[0]) {
      values.companyId = values.companyId[0];
    }
    if (values.contractTypeId && values.contractTypeId[0]) {
      values.contractTypeId = values.contractTypeId[0];
    }
    if (values.partnerId && values.partnerId[0]) {
      values.partnerId = values.partnerId[0];
    }
    this.setState({ searchParams: { ...this.state.searchParams, ...values } }, () => {
      this.customTable.search({ ...this.state.searchParams, ...values });
    });
  };

  // 点击导出按钮
  onExportClick = () => {
    this.setState({
      btLoading: true,
      excelVisible: true,
    });
  };

  // 导出取消
  onExportCancel = () => {
    this.setState({
      btLoading: false,
      excelVisible: false,
    });
  };

  // 确认导出
  export = result => {
    const hide = message.loading('正在生成文件，请等待......');

    const exportParams = this.state.searchParams;
    contractService
      .export(result, exportParams)
      .then(res => {
        if (res.status === 200) {
          message.success('操作成功');
          const fileName = res.headers['content-disposition'].split('filename=')[1];
          const f = new Blob([res.data]);
          FileSaver.saveAs(f, decodeURIComponent(fileName));
          this.setState({
            btLoading: false,
          });
          hide();
        }
      })
      .catch(e => {
        message.error('下载失败，请重试!');
        this.setState({
          btLoading: false,
        });
        hide();
      });
  };

  // 弹出框关闭
  onClose = () => {
    this.setState({
      showChild: false,
      headerData: [],
    });
  };

  render() {
    // 查询条件
    const { searchForm, showChild } = this.state;
    // 返回列表
    const { columns, contractHeaderId, showContract } = this.state;
    // 弹窗
    const { headerData } = this.state;
    // 导出
    const { exportColumns, excelVisible, btLoading } = this.state;
    return (
      <div>
        <SearchArea // 查询条件
          searchForm={searchForm}
          eventHandle={this.eventHandle}
          submitHandle={this.search}
          clearHandle={this.handleReset}
          maxLength={4}
          wrappedComponentRef={inst => (this.formRef = inst)}
        />
        <div style={{ margin: '10px 0' }}>
          <Row>
            <Col span={18}>
              <Button loading={btLoading} type="primary" onClick={this.onExportClick}>
                导出合同
              </Button>
            </Col>
            <Col span={6}>
              <Search
                placeholder={this.$t('my.please.input.number')}
                onSearch={this.searchNumber}
                onChange={this.change}
                enterButton
              />
            </Col>
          </Row>
        </div>
        <CustomTable
          ref={ref => (this.customTable = ref)}
          scroll={{ x: 1850 }}
          columns={columns}
          tableKey="id"
          onClick={this.rowClick}
          url={`${config.contractUrl}/api/contract/header/finance/query`}
        />
        <ExcelExporter // 导出
          visible={excelVisible}
          onOk={this.export}
          columns={exportColumns}
          canCheckVersion={false}
          fileName="合同"
          onCancel={this.onExportCancel}
          excelItem="CONTRACT_FINANCIAL_QUERY"
        />
        <Modal
          destroyOnClose
          title="关联信息"
          visible={showChild}
          footer={[null]}
          width={1200}
          onCancel={this.onClose}
        >
          <RelationInfo headerData={headerData} />
        </Modal>
        <Modal
          title="合同详情"
          visible={showContract}
          onCancel={() => {
            this.setState({ showContract: false });
          }}
          width="90%"
          bodyStyle={{
            maxHeight: '70vh',
            overflow: 'auto',
            padding: '0 10px',
          }}
          footer={null}
          destroyOnClose
        >
          <ContractDetail id={contractHeaderId} isApprovePage={true} />
        </Modal>
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    user: state.user.currentUser,
    company: state.user.company,
  };
}

const wrappedMyContract = Form.create()(ContractView);

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(wrappedMyContract);
