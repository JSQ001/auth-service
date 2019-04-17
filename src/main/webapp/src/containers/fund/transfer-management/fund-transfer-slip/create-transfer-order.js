import React from 'react';
import { Form, Row, Button, Col, Input } from 'antd';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import Table from 'widget/table';
import FundSearchForm from '../../fund-components/fund-search-form';
import 'styles/fund/account.scss';

const { Search } = Input;

class CreateTransferOrder extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      tableData: [],
      searchForm: [
        {
          colSpan: 6,
          type: 'input',
          label: '单据类型',
          id: 'documentType',
        },
        {
          colSpan: 6,
          type: 'modalList',
          label: '单据公司',
          id: 'documentCompany',
          listType: 'company',
          labelKey: 'name',
          listExtraParams: { setOfBooksId: props.company.setOfBooksId },
          valueKey: 'id',
          single: true,
        },
        {
          colSpan: 6,
          type: 'input',
          label: '制单人',
          id: 'createdBy',
        },
        {
          colSpan: 6,
          type: 'valueList',
          label: '审批状态',
          id: 'approvalStatus',
          options: [],
          valueListCode: 'ZJ_BILL_STATUS',
        },
        {
          colSpan: 6,
          type: 'intervalDate',
          id: 'intervalDate',
          fromlabel: '单据日期从',
          fromId: 'dateFrom',
          tolabel: '单据日期至',
          toId: 'dateTo',
        },
        {
          colSpan: 6,
          type: 'input',
          label: '笔数',
          id: 'items',
        },
        {
          colSpan: 6,
          type: 'input',
          label: '金额',
          id: 'amount',
        },
        {
          colSpan: 6,
          type: 'valueList',
          label: '付款方式',
          id: 'paymentPurpose',
          options: [],
          valueListCode: 'ZJ_PAYMENT_TYPE',
        },
      ],
      columns: [
        {
          title: '单据编号',
          dataIndex: 'documentNumber',
          width: 100,
        },
        {
          title: '单据类型',
          dataIndex: 'documentType',
          width: 100,
        },
        {
          title: '所属公司',
          dataIndex: 'company',
          width: 150,
        },
        {
          title: '单据日期',
          dataIndex: 'billDate',
          width: 150,
        },
        {
          title: '付款方式',
          dataIndex: 'paymentMethod',
          width: 150,
        },
        {
          title: '笔数',
          dataIndex: 'account',
          width: 50,
        },
        {
          title: '金额',
          dataIndex: 'amount',
          width: 100,
        },
        {
          title: '描述',
          dataIndex: 'description',
          width: 200,
        },
        {
          title: '制单人',
          dataIndex: 'originator',
          width: 100,
        },
        {
          title: '审批状态',
          dataIndex: 'approveStatusDesc',
          width: 100,
        },
      ],
    };
  }

  create = () => {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: '/transfer-management/fund-transfer-slip/new-fund-transfer-list',
      })
    );
  };

  render() {
    const { searchForm, columns, tableData } = this.state;

    return (
      <div className="train">
        <div className="common-top-area">
          <Row>
            <FundSearchForm
              submitHandle={this.handleSearch}
              searchForm={searchForm}
              maxLength={4}
            />
          </Row>
        </div>
        <div className="table-header">
          <div className="table-header-buttons">
            <Row>
              <Col span={8}>
                <Button
                  type="primary"
                  onClick={e => {
                    e.preventDefault();
                    e.stopPropagation();
                    this.create();
                  }}
                >
                  新建
                </Button>
                <Button
                  type="danger"
                  onClick={e => {
                    e.preventDefault();
                    e.stopPropagation();
                    this.create();
                  }}
                >
                  删除
                </Button>
              </Col>
              <Col span={8} offset={8}>
                <Search
                  placeholder="资金调拨单号"
                  enterButton
                  onSearch={this.searchByDocumentNumber}
                />
              </Col>
            </Row>
          </div>
        </div>
        <Table
          rowKey={record => record.id}
          dataSource={tableData}
          columns={columns}
          bordered
          size="middle"
        />
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

export default connect(mapStateToProps)(Form.create()(CreateTransferOrder));
