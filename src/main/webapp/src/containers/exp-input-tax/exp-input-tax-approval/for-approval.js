import React, { Component } from 'react';
import SearchArea from 'widget/search-area';
import CustomTable from 'components/Widget/custom-table';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import { Input, Popover, Row, Col } from 'antd';
import moment from 'moment';

const { Search } = Input;

class ForApproval extends Component {
  constructor(props) {
    super(props);
    this.state = {
      searchForm: [
        {
          type: 'list',
          options: [],
          label: this.$t('acp.company'),
          id: 'companyId',
          colSpan: 6,
          listType: 'company',
          labelKey: 'name',
          valueKey: 'id',
          listExtraParams: { setOfBooksId: props.company.setOfBooksId },
          single: true,
        },
        {
          type: 'value_list',
          label: this.$t('tax.business.categories'),
          id: 'transferType',
          options: [],
          valueListCode: 'transferType',
          colSpan: 6,
        },
        {
          type: 'value_list',
          label: this.$t('tax.use.type'),
          id: 'useType',
          options: [],
          valueListCode: 'useType',
          colSpan: 6,
        },
        {
          type: 'items',
          id: 'date',
          items: [
            { type: 'date', id: 'transferDateFrom', label: this.$t('tax.businessDate.from') },
            { type: 'date', id: 'transferDateTo', label: this.$t('tax.businessDate.to') },
          ],
          colSpan: 6,
        },
        {
          type: 'list',
          listType: 'select_authorization_user',
          options: [],
          id: 'applicantId',
          label: this.$t('common.applicant'),
          labelKey: 'userName',
          valueKey: 'userId',
          single: true,
          colSpan: 6,
        },
        {
          type: 'list',
          id: 'departmentId',
          label: this.$t('common.department'),
          listType: 'department',
          labelKey: 'name',
          valueKey: 'departmentId',
          single: true,
          colSpan: 6,
        },
        {
          type: 'items',
          id: 'amount',
          colSpan: 6,
          items: [
            { type: 'inputNumber', id: 'amountFrom', label: this.$t('exp.money.from') },
            { type: 'inputNumber', id: 'amountTo', label: this.$t('exp.money.to') },
          ],
        },
        {
          type: 'input',
          id: 'description',
          colSpan: 6,
          label: this.$t('common.remark'),
        },
      ],
      columns: [
        {
          title: this.$t('acp.requisitionNumber'),
          dataIndex: 'documentNumber',
          width: 150,
          render: (value, record) => {
            return (
              <Popover content={value}>
                <a
                  onClick={e => {
                    this.handleToDetails(e, record);
                  }}
                >
                  {value}
                </a>
              </Popover>
            );
          },
        },
        {
          title: this.$t('common.applicant'),
          dataIndex: 'fullName',
          width: 120,
          tooltips: true,
        },
        {
          title: this.$t('acp.company'),
          dataIndex: 'companyName',
          width: 150,
          tooltips: true,
        },
        {
          title: this.$t('common.department'),
          dataIndex: 'departmentName',
          width: 150,
          tooltips: true,
        },
        {
          title: this.$t('tax.business.date'),
          dataIndex: 'transferDate',
          width: 150,
          render: text => {
            return (
              <Popover content={moment(text).format('YYYY-MM-DD')}>
                <span>{moment(text).format('YYYY-MM-DD')}</span>
              </Popover>
            );
          },
        },
        {
          title: this.$t('tax.business.categories'),
          dataIndex: 'transferTypeName',
          width: 150,
          tooltips: true,
        },
        {
          title: this.$t('tax.use.type'),
          dataIndex: 'useTypeName',
          width: 150,
          tooltips: true,
        },
        {
          title: this.$t('common.amount'),
          dataIndex: 'amount',
          width: 150,
          render: (amount, record) => (
            <Popover
              content={
                <span>
                  {`${record.currencyCode} `}
                  {this.filterMoney(amount, 2, true)}
                </span>
              }
            >
              <div style={{ textAlign: 'right' }}>
                {`${record.currencyCode} `}
                {this.filterMoney(amount, 2, true)}
              </div>
            </Popover>
          ),
        },
        {
          title: this.$t('common.remark'),
          dataIndex: 'description',
          width: 150,
          tooltips: true,
        },
      ],
      searchParams: {},
    };
  }

  // 搜索
  handleSearch = value => {
    const tempValue = { ...value };
    if (value.transferDateFrom) {
      tempValue.transferDateFrom = moment(tempValue.transferDateFrom).format('YYYY-MM-DD');
    }
    if (value.transferDateTo) {
      tempValue.transferDateTo = moment(tempValue.transferDateTo).format('YYYY-MM-DD');
    }
    this.setState({ searchParams: { ...tempValue } }, () => {
      const { searchParams } = this.state;
      this.table.search(searchParams);
    });
  };

  // 业务单号搜索
  getSearchData = value => {
    const { searchParams } = this.state;
    searchParams.documentNumber = value;
    this.setState({ searchParams }, () => {
      this.table.search(searchParams);
    });
  };

  // 清空搜索
  handleClear = () => {
    this.setState({ searchParams: {} }, () => {
      const { searchParams } = this.state;
      this.table.search(searchParams);
    });
  };

  // 行内点击
  handleRowClick = record => {
    this.jumpToDetailsPage(record.id, record.documentOid);
  };

  // 单号点击
  handleToDetails = (e, record) => {
    e.preventDefault();
    this.jumpToDetailsPage(record.id, record.documentOid);
  };

  // 跳转至详情
  jumpToDetailsPage = (id, oid) => {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.replace({
        pathname: `/exp-input-tax/exp-input-tax/approval-details/${id}/${oid}`,
      })
    );
  };

  render() {
    const { searchForm, columns } = this.state;
    const { urlValue } = this.props;
    return (
      <div style={{ padding: '20px 0' }}>
        <SearchArea
          searchForm={searchForm}
          submitHandle={this.handleSearch}
          maxLength={4}
          clearHandle={this.handleClear}
        />
        <Row style={{ margin: '10px 0' }}>
          <Col span={6} push={18}>
            <Search
              placeholder={this.$t('tax.business.number.please.enter')} // "请输入进项税业务单单号"
              onSearch={value => {
                this.getSearchData(value);
              }}
            />
          </Col>
        </Row>
        <CustomTable
          ref={ref => {
            this.table = ref;
          }}
          columns={columns}
          url={urlValue}
          // onRowClick={this.handleRowClick}
          onClick={this.handleRowClick}
          scroll={{ x: 1000 }}
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

export default connect(mapStateToProps)(ForApproval);
