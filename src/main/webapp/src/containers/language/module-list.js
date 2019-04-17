import React, { Component } from 'react';
import { Button, Form } from 'antd';
import CustomTable from '../../components/Template/custom-table';
import SearchArea from 'widget/search-area';
import { routerRedux } from 'dva/router';
import { connect } from 'dva';

class LanguageManager extends Component {
  constructor(props) {
    super(props);
    this.state = {
      modules: [],
      addShow: false,
      searchForm: [
        {
          type: 'input',
          id: 'appCode',
          label: '应用代码',
          colSpan: 6,
        },
        {
          type: 'input',
          id: 'appName',
          label: '应用名称',
          colSpan: 6,
        },
      ],
      columns: [
        {
          title: '应用代码',
          dataIndex: 'appCode',
          render: value => <a>{value}</a>,
        },
        {
          title: '应用名称',
          dataIndex: 'appName',
        },
      ],
    };
  }

  handleClick = record => {
    if (this.props.match.params.langType == 'zh_cn') {
      this.props.dispatch(
        routerRedux.push({
          pathname: `/setting/language/language-setting/${record.id}/${record.appCode}`,
        })
      );
    } else {
      this.props.dispatch(
        routerRedux.push({
          pathname: `/setting/language/other-language-setting/${this.props.match.params.langType}/${
            record.id
          }/${record.appCode}`,
        })
      );
    }
  };

  back = () => {
    this.props.dispatch(
      routerRedux.push({
        pathname: '/setting/language/language',
      })
    );
  };

  search = values => {
    this.refs.table.search(values);
  };

  render() {
    const { columns, addShow, searchForm } = this.state;
    const { getFieldDecorator } = this.props.form;

    const formItemLayout = {
      labelCol: {
        xs: { span: 24 },
        sm: { span: 5 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 19 },
      },
    };

    return (
      <div style={{ backgroundColor: '#fff', padding: 10, overflow: 'auto' }}>
        <SearchArea
          searchForm={searchForm}
          maxLength={4}
          clearHandle={() => {}}
          submitHandle={this.search}
        />

        <Button style={{ margin: '10px 0' }} onClick={this.back}>
          返回上一级
        </Button>
        <CustomTable
          ref="table"
          url="/api/application"
          columns={columns}
          onRowClick={this.handleClick}
        />
      </div>
    );
  }
}

export default connect()(Form.create()(LanguageManager));
