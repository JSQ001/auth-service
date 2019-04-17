/*eslint-disable*/
import React from 'react';
import { connect } from 'dva';
// import CustomTable from 'widget/custom-table';
import { Table, Spin } from 'antd';
import TaxLevelService from './tax-level-service';

class VatInformation extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      dataSource: [],
      columns: [
        {
          title: '纳税人名称',
          dataIndex: 'taxpayerName',
          key: 'taxpayerName',
          width: 250,
        },
        {
          title: '纳税人识别号',
          dataIndex: 'taxpayerNumber',
          key: 'taxpayerNumber',
          width: 150,
        },
        {
          title: '税号类型',
          dataIndex: 'taxpayerNumberTypeName',
          key: 'taxpayerNumberTypeName',
          width: 100,
        },
        {
          title: '纳税资质',
          dataIndex: 'taxQualificationName',
          key: 'taxQualificationName',
          width: 100,
        },
        {
          title: '申报层级',
          dataIndex: 'declareLevel',
          key: 'declareLevel',
          width: 100,
        },
        {
          title: '预缴申报周期',
          dataIndex: 'preDeclarePeriod',
          key: 'preDeclarePeriod',
          width: 100,
        },
        {
          title: '预征率',
          dataIndex: 'preRate',
          key: 'preRate',
          width: 100,
        },
        {
          title: '预缴分配比例',
          dataIndex: 'preDistributionRatio',
          key: 'preDistributionRatio',
          width: 100,
        },
        {
          title: '汇缴申报周期',
          dataIndex: 'remDeclarePeriod',
          key: 'remDeclarePeriod',
          width: 100,
        },
        {
          title: '汇缴分配比例',
          dataIndex: 'remDistributionRatio',
          key: 'remDistributionRatio',
          width: 100,
        },
      ],
      loading: true,
    };
  }
  componentDidMount() {
    TaxLevelService.pageTaxValueAddedTaxInfoByCond1()
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
      temp[item.taxpayerId] = item;
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
        tree[item.taxpayerId] = item;
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

export default connect()(VatInformation);
