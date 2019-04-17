import React from 'react';
import { connect } from 'dva';
import { Button, Menu, Dropdown, Icon, Row, Col, Popconfirm, Popover, message } from 'antd';
import 'styles/my-account/my-account.scss';
import SlideFrame from 'components/Widget/slide-frame';
import NewUpdateExpense from 'containers/my-account/new-update-account';
import expenseService from 'containers/my-account/expense.service';
import { rejectPiwik } from 'share/piwik';
import SearchArea from 'components/Widget/search-area';
import config from 'config';
import CustomTable from 'components/Widget/custom-table';

class MyAccount extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
      data: [],
      title: '',
      record: {},
      searchForm: [
        {
          label: this.$t('setting.key1504'),
          type: 'list',
          id: 'expenseTypeId',
          colSpan: 6,
          listType: 'expense_item',
          labelKey: 'name',
          valueKey: 'id',
          single: true,
          listExtraParams: {
            setOfBooksId: this.props.company.setOfBooksId,
            typeFlag: 1,
            roleType: 'TENANT',
          },
        },
        {
          type: 'items',
          colSpan: 6,
          id: 'dateRange',
          items: [
            {
              type: 'date',
              id: 'dateFrom',
              label: this.$t({ id: 'exp.happen.date.from' } /*发生日期从*/),
              event: 'SIGN_DATE_FROM',
            },
            {
              type: 'date',
              id: 'dateTo',
              label: this.$t('exp.happen.date.to'),
              event: 'SIGN_DATE_TO',
            },
          ],
        },
        {
          type: 'select',
          id: 'currencyCode',
          label: this.$t({ id: 'expense.reverse.currency.code' } /*币种*/),
          options: [],
          colSpan: '6',
          method: 'get',
          getUrl: `${config.mdataUrl}/api/currency/rate/list`,
          listKey: 'records',
          getParams: {
            enable: true,
            setOfBooksId: this.props.company.setOfBooksId,
            tenantId: this.props.company.tenantId,
          },
          valueKey: 'currencyCode',
          labelKey: 'currencyCodeAndName',
          event: 'currency1',
        },
        {
          type: 'items',
          id: 'amount',
          colSpan: 6,
          items: [
            { type: 'input', id: 'amountFrom', label: '金额从' },
            { type: 'input', id: 'amountTo', label: '金额至' },
          ],
        },
      ],
      columns: [
        {
          title: this.$t('common.expense.type') /*费用类型*/,
          align: 'center',
          dataIndex: 'expenseTypeName',
          render: expenseTypeName => <Popover content={expenseTypeName}>{expenseTypeName}</Popover>,
        },
        {
          title: this.$t('common.happened.date') /*日期*/,
          align: 'center',
          dataIndex: 'createdDate',
          render: createdDate => new Date(createdDate).format('yyyy-MM-dd'),
        },
        {
          title: this.$t('common.currency') /*币种*/,
          align: 'center',
          dataIndex: 'currencyCode',
          width: '5%',
        },
        {
          title: this.$t('common.amount') /*金额*/,
          align: 'center',
          dataIndex: 'amount',
          render: desc => this.filterMoney(desc),
        },
        {
          title: this.$t('common.remark') /*备注*/,
          align: 'center',
          dataIndex: 'remarks',
          render: comment => <Popover content={comment}>{comment || '-'}</Popover>,
        },
        /*
        { title: this.$t("common.attachments")/!*附件*!/, dataIndex: 'attachments', width: '7%', render: attachments => attachments.length },
*/
        {
          title: this.$t('common.operation') /*操作*/,
          align: 'center',
          dataIndex: 'operate',
          render: (desc, record) => {
            return (
              <span>
                <a
                  onClick={e =>
                    this.setState({
                      showExpenseFlag: true,
                      record: record,
                      title: this.$t('expense.edit'),
                    })
                  }
                >
                  {this.$t('common.edit')}
                </a>
              </span>
            );
          },
        },
      ],

      nowExpense: null,
      showExpenseFlag: false,
      expenseSource: '',
      businessCardEnabled: false,
      invoiceEnabled: false,
    };
  }

  handleCloseExpense = refresh => {
    this.setState({ showExpenseFlag: false }, () => {
      refresh && this.customTable.search();
    });
  };

  eventHandle = (e, value) => {};

  handleAdd = () => {
    this.setState({
      showExpenseFlag: true,
      record: {},
      title: this.$t('expense.new'),
    });
  };

  search = params => {
    params.dateFrom && (params.dateFrom = params.dateFrom.format('YYYY-MM-DD'));
    params.dateTo && (params.dateTo = params.dateTo.format('YYYY-MM-DD'));
    this.customTable.search(params);
  };

  render() {
    const {
      loading,
      data,
      pagination,
      columns,
      showExpenseFlag,
      nowExpense,
      expenseSource,
      businessCardEnabled,
      invoiceEnabled,
      title,
      searchForm,
      record,
    } = this.state;
    const { user } = this.props;
    return (
      <div className="my-account">
        <SearchArea
          searchForm={searchForm}
          eventHandle={this.eventHandle}
          submitHandle={this.search}
          maxLength={4}
          //wrappedComponentRef={inst => (this.formRef = inst)}
        />
        <div className="operate-area">
          <Button style={{ marginLeft: 8 }} onClick={this.handleAdd} type="primary">
            {this.$t('exp.add') /*记一笔*/}
          </Button>
        </div>
        <CustomTable
          ref={ref => (this.customTable = ref)}
          columns={columns}
          tableKey="id"
          onClick={this.rowClick}
          showNumber={true}
          url={`${config.expenseUrl}/api/expense/book/query`}
        />
        <SlideFrame
          show={showExpenseFlag}
          title={title}
          onClose={e =>
            this.setState({ showExpenseFlag: false, nowExpense: null, expenseSource: '' }, () => {
              e && this.customTable.search();
            })
          }
          width="800px"
        >
          <NewUpdateExpense
            params={{
              record,
              expenseSource,
              slideFrameShowFlag: showExpenseFlag,
              businessCardEnabled,
              user,
            }}
            onClose={this.handleCloseExpense}
          />
        </SlideFrame>
      </div>
    );
  }
}

function mapStateToProps(state) {
  return {
    company: state.user.company,
    user: state.user.currentUser,
  };
}

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(MyAccount);
