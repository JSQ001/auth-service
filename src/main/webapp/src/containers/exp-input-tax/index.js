import React from 'react';
import { connect } from 'dva';
import SearchArea from 'widget/search-area';
import { routerRedux } from 'dva/router';
import { Button, Row, Col, Input, Popover, Badge } from 'antd';
import CustomTable from 'widget/custom-table';
import config from 'config';
import moment from 'moment';

const { Search } = Input;

class expInputTax extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      searchForm: [
        {
          type: 'value_list',
          label: this.$t('tax.business.categories'), // '业务大类',
          id: 'transferType',
          options: [],
          valueListCode: 'transferType',
          colSpan: 6,
        },
        {
          type: 'value_list',
          label: this.$t('tax.use.type'), // '用途类型',
          id: 'useType',
          options: [],
          valueListCode: 'useType',
          colSpan: 6,
        },
        {
          type: 'items',
          id: 'date',
          items: [
            { type: 'date', id: 'transferDateFrom', label: this.$t('tax.businessDate.from') }, // '业务日期从'
            { type: 'date', id: 'transferDateTo', label: this.$t('tax.businessDate.to') }, // '业务日期至'
          ],
          colSpan: 7,
        },
        {
          type: 'value_list',
          label: this.$t({ id: 'budgetJournal.status' }),
          id: 'status',
          options: [
            { value: 1001, label: this.$t('common.editing') }, // '编辑中'
            { value: 1002, label: this.$t('constants.documentStatus.audit.ing') }, // '审核中'
            { value: 1004, label: this.$t('constants.documentStatus.audit.pass') }, // '审核通过'
            { value: 1005, label: this.$t('constants.documentStatus.audit.rejected') }, // '审核驳回'
          ],
          colSpan: 5,
          valueKey: 'value',
          labelKey: 'label',
        },
        {
          type: 'list',
          listType: 'select_authorization_user',
          options: [],
          id: 'applicantId',
          label: this.$t('common.applicant'), //  '申请人'
          labelKey: 'userName',
          valueKey: 'userId',
          single: true,
          colSpan: 6,
          // defaultValue: [{ userName: props.user.fullName, userId: props.user.userId }],
          // disabled: true,
        },
        {
          type: 'items',
          id: 'amount',
          colSpan: 6,
          items: [
            { type: 'inputNumber', id: 'amountFrom', label: this.$t('exp.money.from') }, // '金额从'
            { type: 'inputNumber', id: 'amountTo', label: this.$t('exp.money.to') }, // '金额至'
          ],
        },
        {
          type: 'input',
          id: 'description',
          colSpan: 7,
          label: this.$t('common.remark'), // '备注'
        },
      ],
      columns: [
        {
          title: this.$t('acp.requisitionNumber'), // '单据编号'
          dataIndex: 'documentNumber',
          align: 'center',
          width: 180,
          render: (value, record) => {
            return (
              <Popover content={value}>
                <a>{value}</a>
              </Popover>
            );
          },
        },
        {
          title: this.$t('common.applicant'), // '申请人'
          dataIndex: 'fullName',
          align: 'center',
          width: 120,
          tooltips: true,
        },
        {
          title: this.$t('tax.business.date'), // '业务日期'
          dataIndex: 'transferDate',
          align: 'center',
          width: 120,
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
          align: 'center',
          width: 150,
          tooltips: true,
        },
        {
          title: this.$t('tax.use.type'),
          dataIndex: 'useTypeName',
          align: 'center',
          width: 150,
          tooltips: true,
        },
        {
          title: this.$t('contract.amount'), // '金额'
          dataIndex: 'amount',
          align: 'center',
          width: 100,
          tooltips: true,
        },
        {
          title: this.$t('common.remark'),
          dataIndex: 'description',
          align: 'center',
          width: 200,
          tooltips: true,
        },
        {
          title: this.$t('common.column.status'), // '状态'
          dataIndex: 'status',
          align: 'center',
          width: 100,
          render: status => {
            if (String(status) === '1002') {
              status = 3002;
            } else if (String(status) === '1004') {
              status = 1006;
            } else if (String(status) === '1005') {
              status = 1007;
            }
            return (
              <Popover content={this.$statusList[status].label}>
                <Badge
                  status={this.$statusList[status].state}
                  text={this.$statusList[status].label}
                />
              </Popover>
            );
          },
        },
      ],
      searchParams: {},
    };
  }

  // 搜索
  search = values => {
    const { searchParams } = this.state;
    this.setState(
      {
        searchParams: {
          ...values,
          applicantId: values.applicantId && values.applicantId[0] ? values.applicantId[0] : '',
          transferDateFrom:
            values.transferDateFrom && moment(values.transferDateFrom).format('YYYY-MM-DD'),
          transferDateTo:
            values.transferDateTo && moment(values.transferDateTo).format('YYYY-MM-DD'),
        },
      },
      () => {
        const { searchParams } = this.state;
        this.table.search(searchParams);
      }
    );
  };

  // 清除
  clear = () => {
    this.setState({ searchParams: {} }, () => {
      const { searchParams } = this.state;
      this.table.search(searchParams);
    });
  };

  // 单号搜索
  searchNumber = value => {
    if (value) {
      const { searchParams } = this.state;
      searchParams.documentNumber = value;
      this.setState({ searchParams }, () => {
        const { searchParams } = this.state;
        this.table.search(searchParams);
      });
    } else this.table.search();
  };

  // 新建页面
  newReimburseForm = () => {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: '/exp-input-tax/exp-input-tax/header-edit',
      })
    );
  };

  // 跳转到详情
  handleRowClick = record => {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/exp-input-tax/exp-input-tax/input-tax-business-receipt/${record.id}/${
          record.transferType
        }`,
      })
    );
  };

  render() {
    const { searchForm, columns } = this.state;
    const { user } = this.props;
    return (
      <div className="reimburse-container">
        <SearchArea
          searchForm={searchForm}
          submitHandle={this.search}
          maxLength={4}
          clearHandle={this.clear}
        />

        <Row style={{ marginBottom: 10, marginTop: 10 }}>
          <Col id="application-form-drop" style={{ position: 'relative' }} span={18}>
            <div className="table-header-buttons">
              <Button type="primary" onClick={this.newReimburseForm}>
                {this.$t('tax.input.tax.business.form.new')}
              </Button>{' '}
              {/* 新建进项税业务单 */}
            </div>
          </Col>
          <Col span={6}>
            <Search
              placeholder={this.$t('tax.business.number.please.enter')} // "请输入进项税业务单单号"
              style={{ width: '100%' }}
              onSearch={this.searchNumber}
              enterButton
              allowClear
            />
          </Col>
        </Row>
        <CustomTable
          onClick={this.handleRowClick}
          ref={ref => {
            this.table = ref;
          }}
          columns={columns}
          url={`${config.expenseUrl}/api/input/header/query`}
          // params={{ applicantId: user.userId }}
          onRowClick={this.handleRowClick}
        />
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
export default connect(mapStateToProps)(expInputTax);
