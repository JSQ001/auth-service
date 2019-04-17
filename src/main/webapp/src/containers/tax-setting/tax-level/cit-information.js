/*eslint-disable*/
import React from 'react';
import { connect } from 'dva';
import CustomTable from 'widget/custom-table';
import { Table, Spin, Tag } from 'antd';
import TaxLevelService from './tax-level-service';

class CitInformation extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      dataSource: [],
      columns: [
        {
          title: '纳税人名称',
          dataIndex: 'taxpayerName',
          key: 'taxpayerName',
        },
        {
          title: '纳税人识别号',
          dataIndex: 'taxpayerNumber',
          key: 'taxpayerNumber',
        },
        {
          title: '税号类型',
          dataIndex: 'taxpayerNumberTypeName',
          key: 'taxpayerNumberTypeName',
        },
        {
          title: '纳税资质',
          dataIndex: 'taxQualificationName',
          key: 'taxQualificationName',
        },
        {
          title: '申报层级',
          dataIndex: 'declareLevel',
          key: 'declareLevel',
        },
        {
          title: '预缴申报周期',
          dataIndex: 'preDeclarePeriod',
          key: 'preDeclarePeriod',
        },
        {
          title: '汇缴申报周期',
          dataIndex: 'remDeclarePeriod',
          key: 'remDeclarePeriod',
        },
      ],
      loading: true,
    };
  }
  componentDidMount() {
    TaxLevelService.pageTaxValueAddedTaxInfoByCond()
      .then(res => {
        let data = res.data;
        console.log(data);
        let data1 = this.buildTree(data);
        this.setState({
          dataSource: data1,
          loading: false,
        });
      })
      .catch(err => {
        message.error(err.response.data.message);
      });
  }
  buildTree(data) {
    let temp = {};
    let tree = {};
    data.forEach(item => {
      temp[item.id] = item;
    });

    let tempKeys = Object.keys(temp);
    tempKeys.forEach(key => {
      let item = temp[key];

      if (item.parentTaxpayerId == null) {
        item.parentTaxpayerId = -1;
      }
      let _itemPId = item.parentTaxpayerId;
      let parentItemByPid = temp[_itemPId];
      if (parentItemByPid) {
        if (!parentItemByPid.children) {
          parentItemByPid.children = [];
        }
        parentItemByPid.children.push(item);
      } else {
        tree[item.id] = item;
      }
    });
    return Object.keys(tree).map(key => tree[key]);
  }
  render() {
    let { dataSource, columns } = this.state;
    return (
      <Spin spinning={this.state.loading}>
        <Table dataSource={dataSource} columns={columns} scroll={{ x: 1500 }} rowKey="id" />
      </Spin>
    );
  }
}

export default connect()(CitInformation);
