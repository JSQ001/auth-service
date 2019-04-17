import React, { Component } from 'react';
import { connect } from 'dva';
import config from 'config';
import { routerRedux } from 'dva/router';
import CustomTable from 'widget/custom-table';
import SearchArea from 'widget/search-area';

class InterfaceManager extends Component {
  constructor(props) {
    super(props);
    this.columns = [
      {
        title: this.$t('app.info.code' /*应用代码*/),
        dataIndex: 'appCode',
        width: 200,
        render: (value, record) => <a onClick={() => this.toInterfaceList(record.id)}>{value}</a>,
      },
      {
        title: this.$t('app.info.name' /*应用名称*/),
        dataIndex: 'appName',
        width: 200,
      },
    ];
    this.searchForm = [
      {
        type: 'input',
        id: 'appCode',
        label: this.$t('app.info.code' /*应用代码*/),
        colSpan: 6,
      },
      {
        type: 'input',
        id: 'appName',
        label: this.$t('app.info.name' /*应用名称*/),
        colSpan: 6,
      },
    ];
  }

  search = values => {
    this.table.search(values);
  };

  clear = () => {
    this.table.reload();
  };

  toInterfaceList = id => {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: '/setting/interface/interface-list/' + id,
      })
    );
  };

  render() {
    return (
      <div>
        <SearchArea
          searchForm={this.searchForm}
          submitHandle={this.search}
          clearHandle={this.clear}
        />
        <div style={{ marginTop: 12 }}>
          <CustomTable
            columns={this.columns}
            url={`${config.baseUrl}/api/application`}
            ref={ref => (this.table = ref)}
          />
        </div>
      </div>
    );
  }
}
export default connect()(InterfaceManager);
