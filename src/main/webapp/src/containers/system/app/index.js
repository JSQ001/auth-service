/**
 * Created by weishan on 2019/03/07.
 * 应用管理
 */
import React from 'react';
import { Button, Badge, message, Tooltip, Tag } from 'antd';
import SlideFrame from 'components/Widget/slide-frame';

import NewApp from './app-info';
import SearchArea from 'widget/search-area';
import CustomTable from 'widget/custom-table';
import config from 'config';

class App extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      buttonFlag: false,
      loading: false,
      searchParams: {},
      columns: [
        {
          title: this.$t('app.info.code' /*应用代码*/),
          dataIndex: 'appCode',
          align: 'left',
        },
        {
          title: this.$t('app.info.name' /*应用名称*/),
          dataIndex: 'appName',
          align: 'left',
          render: (value, record) => (
            <Tooltip title={record.userName}>
              <Tag color="green">{value}</Tag>
            </Tooltip>
          ),
        },
        {
          title: this.$t('common.column.status' /*状态*/),
          dataIndex: 'status',
          align: 'center',
          render: status => (
            <Badge
              status={status == 'UP' ? 'success' : 'error'}
              text={status == 'UP' ? this.$t('app.status.up') : this.$t('app.status.down')}
            />
          ),
        },
      ],
      searchForm: [
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
      ],
      showListSelector: false,
      updateParams: {},
      showSlideFrameNew: false,
    };
  }

  editItem = record => {
    console.log(record);
    this.setState({
      updateParams: record,
      showSlideFrameNew: true,
    });
  };

  handleCloseNewSlide = params => {
    this.setState({ showSlideFrameNew: false });
    params && this.table.reload();
  };

  showSlideNew = flag => {
    this.setState({
      showSlideFrameNew: flag,
    });
  };

  newItemShowSlide = () => {
    this.setState(
      {
        updateParams: { record: {} },
      },
      () => {
        this.showSlideNew(true);
      }
    );
  };

  search = values => {
    this.table.search(values);
  };

  // 清除
  clearFunction = () => {
    this.handleSearch();
  };

  render() {
    const { columns, searchParams, searchForm, updateParams, showSlideFrameNew } = this.state;
    return (
      <div style={{ paddingBottom: 20 }} className="value-list">
        <SearchArea
          maxLength={4}
          searchParams={searchParams}
          submitHandle={this.search}
          clearHandle={this.clearFunction}
          searchForm={searchForm}
        />
        <div className="table-header">
          <div className="table-header-buttons" style={{ paddingTop: 15 }}>
            <Button type="primary" onClick={this.newItemShowSlide} style={{ marginRight: 15 }}>
              {/*新增应用*/}
              {this.$t('common.create')}
            </Button>
          </div>
        </div>
        <CustomTable
          ref={ref => (this.table = ref)}
          showNumber={true}
          onClick={this.editItem}
          columns={columns}
          url={`${config.baseUrl}/api/application`}
        />
        {console.log(updateParams)}
        <SlideFrame
          title={updateParams.id ? this.$t('common.edit') : this.$t('common.create')}
          show={showSlideFrameNew}
          onClose={() => this.setState({ showSlideFrameNew: false })}
        >
          <NewApp
            onClose={this.handleCloseNewSlide}
            params={{ ...updateParams, visible: showSlideFrameNew }}
          />
        </SlideFrame>
      </div>
    );
  }
}

export default App;
