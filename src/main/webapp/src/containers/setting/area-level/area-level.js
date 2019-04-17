import React from 'react';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import { Button, message, Badge } from 'antd';
import config from 'config';
import SearchArea from 'widget/search-area';
import CustomTable from 'widget/custom-table';
import service from './service';
import { messages } from 'utils/utils';
import baseService from 'share/base.service';

class AreaLevel extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      pagination: {
        total: 0,
      },
      btnLoad: false,
      searchParams: { setOfBooksId: this.props.company.setOfBooksId },
      columns: [
        {
          title: '账套',
          dataIndex: 'setOfBooksName',
          align: 'center',
          width: 150,
          tooltips: true,
        },
        {
          title: '地点级别代码',
          dataIndex: 'code',
          align: 'center',
          width: 150,
          tooltips: true,
        },
        {
          title: '地点级别名称',
          dataIndex: 'name',
          align: 'center',
          width: 100,
        },
        {
          title: '备注',
          dataIndex: 'remarks',
          align: 'center',
          tooltips: true,
          width: 120,
        },
        {
          title: '状态',
          dataIndex: 'enabled',
          width: 100,
          align: 'center',
          render: enabled => (
            <Badge status={enabled ? 'success' : 'error'} text={enabled ? '启用' : '禁用'} />
          ),
        },
      ],
      searchForm: [
        {
          type: 'select',
          id: 'setOfBooksId',
          label: '账套',
          options: [],
          labelKey: 'name',
          valueKey: 'id',
          isRequired: true,
          defaultValue: this.props.company.setOfBooksId,
          colSpan: '6',
        },
        {
          type: 'input',
          id: 'code',
          label: '地点级别代码',
          colSpan: '6',
        },
        {
          type: 'input',
          id: 'name',
          label: '地点级别名称',
          colSpan: '6',
        },
        {
          type: 'select',
          id: 'enbaled',
          label: '状态',
          options: [{ value: true, label: '启用' }, { value: false, label: '禁用' }],
          labelKey: 'name',
          valueKey: 'id',
          defaultValue: this.props.company.setOfBooksId,
          colSpan: '6',
        },
      ],
      setOfBooks: [],
    };
  }

  componentWillMount() {
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
      this.setState({ searchForm: form, setOfBooks: list });
    });
  };

  getList = () => {
    let { searchParams } = this.state;
    this.table.search(searchParams);
  };
  //搜索
  search = values => {
    this.setState({ searchParams: { ...this.state.searchParams, ...values } }, () => {
      this.getList();
    });
  };

  //清除
  clear = () => {
    this.setState({ searchParams: {} }, () => {
      this.getList();
    });
  };
  render() {
    const { btnLoad, searchForm, columns, searchParams } = this.state;
    return (
      <div className="table-header">
        <SearchArea
          searchForm={searchForm}
          submitHandle={this.search}
          maxLength={4}
          clearHandle={this.clear}
        />
        <div className="table-header-buttons">
          <Button loading={btnLoad} type="primary" onClick={this.createHandle}>
            新建地点级别
          </Button>
        </div>
        <CustomTable
          ref={ref => (this.table = ref)}
          columns={columns}
          url={`${config.mdataUrl}/api/location/level/query`}
          params={searchParams}
        />
      </div>
    );
  }
}
function mapStateToProps(state) {
  return {
    company: state.user.company,
  };
}

export default connect(
  mapStateToProps,
  null,
  null,
  { withRef: true }
)(AreaLevel);
