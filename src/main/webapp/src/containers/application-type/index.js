import React, { Component } from 'react';
import CustomTable from 'widget/custom-table';
import SearchArea from 'widget/search-area';
import SlideFrame from 'widget/slide-frame';
import NewApplicationType from './new';

import config from 'config';
import { connect } from 'dva';
import { Badge, Button, Divider } from 'antd';
import baseService from 'share/base.service';

import { routerRedux } from 'dva/router';

class ApplicationType extends Component {
  constructor(props) {
    super(props);
    this.state = {
      columns: [
        {
          title: this.$t({ id: 'expense.policy.setOfBooksName' }) /*账套*/,
          dataIndex: 'setOfBooksName',
          align: 'center',
          tooltips: true,
        },
        {
          title: this.$t({ id: 'expense.type.of.application.code' }) /*费用申请单类型代码*/,
          dataIndex: 'typeCode',
          align: 'center',
        },
        {
          title: this.$t({ id: 'expense.application.form.type.name' }) /*费用申请单类型名称*/,
          dataIndex: 'typeName',
          align: 'center',
          tooltips: true,
        },
        {
          title: this.$t({ id: 'billing.expense.associationType' }) /*关联表单类型*/,
          dataIndex: 'formName',
          align: 'center',
        },
        {
          title: this.$t({ id: 'expense.policy.enabled' }) /*状态*/,
          dataIndex: 'enabled',
          width: 120,
          align: 'center',
          render: value =>
            value ? (
              <Badge status="success" text={this.$t('expense.reverse.status')} />
            ) : (
              <Badge status="error" text={this.$t('expense.disable')} />
            ) /*启用*/ /*禁用*/,
        },
        {
          title: this.$t({ id: 'expense.the.dimension' }) /*维度*/,
          dataIndex: 'id',
          width: 120,
          align: 'center',
          render: value => (
            <a onClick={() => this.dimensionSetting(value)}>
              {this.$t('expense.dimension.setting')}
            </a>
          ) /*维度设置*/,
        },
        {
          title: this.$t({ id: 'expense.operation' }) /*操作*/,
          dataIndex: 'options',
          width: 150,
          align: 'center',
          render: (value, record) => (
            <span>
              <a onClick={() => this.edit(record)}>{this.$t('expense.the.editor')}</a>
              {/*编辑*/}
              <Divider type="vertical" />
              <a onClick={() => this.distributionCompany(record)}>
                {this.$t('expense.distribution.of.the.company')}
              </a>
              {/*公司分配*/}
            </span>
          ),
        },
      ],
      searchForm: [
        {
          type: 'select',
          colSpan: '6',
          id: 'setOfBooksId',
          label: this.$t({ id: 'pre.payment.setOfBookName' } /*账套*/),
          options: [],
          labelKey: 'name',
          valueKey: 'id',
          isRequired: true,
          event: 'setOfBooksId',
          allowClear: false,
          defaultValue:
            props.match.params.setOfBooksId == '0'
              ? props.company.setOfBooksId
              : props.match.params.setOfBooksId,
        },
        {
          type: 'input',
          colSpan: '6',
          id: 'typeCode',
          label: this.$t({ id: 'expense.type.of.application.code' }) /*申请单类型代码*/,
        },
        {
          type: 'input',
          colSpan: '6',
          id: 'typeName',
          label: this.$t({ id: 'expense.application.type.name' }) /*申请单类型名称*/,
        },
        {
          type: 'select',
          colSpan: '6',
          id: 'enabled',
          label: this.$t({ id: 'expense.policy.enabled' }),
          options: [
            { label: this.$t({ id: 'expense.reverse.status' }), value: 1 },
            { label: this.$t({ id: 'expense.disable' }), value: 0 },
          ] /*禁用*/ /*状态*/ /*启用*/,
          labelKey: 'label',
          valueKey: 'value',
          event: 'enabled',
        },
      ],
      // setOfBooksId: props.company.setOfBooksId,
      setOfBooksId:
        props.match.params.setOfBooksId === '0'
          ? props.company.setOfBooksId
          : props.match.params.setOfBooksId,
      searchParams: {},
      visible: false,
      record: {},
    };
  }

  componentDidMount() {
    this.getSetOfBookList();
  }

  //获取账套列表
  getSetOfBookList = () => {
    baseService.getSetOfBooksByTenant().then(res => {
      let list = [];
      res.data.map(item => {
        list.push({ value: item.id, label: `${item.setOfBooksCode}-${item.setOfBooksName}` });
      });
      let form = this.state.searchForm;
      form[0].options = list;
      // form[0].defaultValue = this.props.company.setOfBooksId;
      form[0].defaultValue =
        this.props.match.params.setOfBooksId == '0'
          ? this.props.company.setOfBooksId
          : this.props.match.params.setOfBooksId;
      this.setState({ searchForm: form, setOfBooksId: form[0].defaultValue });
    });
  };

  search = values => {
    this.setState({ searchParams: values }, () => {
      this.table.search(values);
    });
  };

  clear = () => {
    this.setState(
      {
        setOfBooksId: this.props.company.setOfBooksId,
        searchParams: {},
      },
      () => {
        this.table.reload();
      }
    );
  };

  formChange = (event, value) => {
    if (event == 'setOfBooksId') {
      this.setState(
        { setOfBooksId: value, searchParams: { ...this.state.searchParams, setOfBooksId: value } },
        () => {
          this.table.search(this.state.searchParams);
        }
      );
    } else if (event == 'enabled') {
      this.setState({ searchParams: { ...this.state.searchParams, enabled: value } }, () => {
        this.table.search(this.state.searchParams);
      });
    }
  };

  //新建
  add = () => {
    this.setState({ visible: true });
  };

  //维度设置
  dimensionSetting = id => {
    this.props.dispatch(
      routerRedux.push({
        pathname: '/document-type-manage/application-type/dimension-setting/' + id,
      })
    );
  };

  //编辑
  edit = record => {
    this.setState({ visible: true, record: { ...record } });
  };

  //分配公司
  distributionCompany = record => {
    this.props.dispatch(
      routerRedux.push({
        pathname: '/document-type-manage/application-type/distribution-company/' + record.id,
      })
    );
  };

  //侧拉框关闭
  close = flag => {
    this.setState({ visible: false, record: {} }, () => {
      if (flag) {
        this.table.search(this.state.searchParams);
      }
    });
  };

  render() {
    let { columns, searchForm, visible, record, setOfBooksId } = this.state;
    return (
      <div>
        <SearchArea
          submitHandle={this.search}
          searchForm={searchForm}
          clearHandle={this.clear}
          eventHandle={this.formChange}
        />
        <div style={{ margin: '20px 0' }}>
          <Button onClick={this.add} type="primary">
            {this.$t('expense.the.new.building')}
          </Button>
          {/*新 建*/}
        </div>
        <CustomTable
          ref={ref => (this.table = ref)}
          columns={columns}
          url={`${config.expenseUrl}/api/expense/application/type/query`}
          params={{ setOfBooksId: setOfBooksId }}
          size="middle"
        />
        <SlideFrame
          title={record.id ? this.$t('expense.the.editor') : this.$t('expense.the.new.building')}
          show={visible}
          onClose={() => {
            this.setState({ visible: false, record: {} });
          }}
        >
          {/*编辑*/}
          {/*新建*/}
          <NewApplicationType
            setOfBooks={searchForm[0].options}
            close={this.close}
            params={{ id: record.id, setOfBooksId: setOfBooksId }}
          />
        </SlideFrame>
      </div>
    );
  }
}

export default connect(state => ({ company: state.user.company }))(ApplicationType);
