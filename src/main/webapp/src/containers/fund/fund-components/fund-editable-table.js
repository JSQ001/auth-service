import React from 'react';
import { connect } from 'dva';
import { Input, Form } from 'antd';
import Table from 'widget/table';
import Lov from 'widget/Template/lov';
import FundCompanyLov from './fund-company-lov';
import FundAccountLov from './fund-account-lov';
import { sliceArray } from './utils';
import 'styles/fund/editable.scss';

const FormItem = Form.Item;
class FundEditableTable extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      copyDataSource: [],
      copyColumns: [],
      count: 0, // 数量
      accountLovPrams: {}, // 账号需要的参数
    };
  }

  componentDidMount() {
    const { columns, onRef } = this.props;
    onRef(this);
    this.getCopyColumns(columns);
  }

  componentWillReceiveProps(nextProps) {
    const { copyDataSource } = this.state;
    const { dataSource, updateTableData } = nextProps;
    // 防止form表单触发componentWillReceiveProps改变table的dataSource
    if (copyDataSource.length === 0) {
      this.getCopyDataSource(dataSource);
    }
    if (updateTableData) {
      this.getCopyDataSource(dataSource);
    }
  }

  /**
   * 对dataSource处理
   */
  getCopyDataSource = dataSource => {
    const copyDataSource = dataSource.map((item, index) => {
      return {
        ...item,
        key: index,
        sequenceNumber: index + 1,
      };
    });
    this.setState({
      copyDataSource,
      count: dataSource.length,
    });
    return copyDataSource;
  };

  getItemInitialValue = (type, text, record) => {
    if (type === 'fundCompanyLov') {
      return { key: record.adjustInCorp, lable: record.adjustInCorpDesc };
    } else if (type === 'fundAccountLov') {
      return { key: record.adjustInAccountId, lable: record.adjustInAccount };
    }
    return text;
  };

  /**
   * 对columns处理
   */
  getCopyColumns = columns => {
    const copyColumns = columns.map(col => {
      const {
        form: { getFieldDecorator },
      } = this.props;
      // 区分配置项是否可编辑
      if (!col.editable) {
        return col;
      }
      return {
        ...col,
        render: (text, record, index) => {
          const itemInitialValue = this.getItemInitialValue(col.type, text, record);
          return (
            <FormItem style={{ marginBottom: 0 }}>
              {getFieldDecorator(`${index}-${col.saveDataIndex}`, {
                rules: [
                  { required: col.required === false ? col.required : true, message: '请输入' },
                ],
                initialValue: itemInitialValue || col.initialValue,
              })(this.renderFormItem(col, index, `${index}-${col.saveDataIndex}`))}
            </FormItem>
          );
        },
      };
    });

    this.setState({
      copyColumns,
    });
    return copyColumns;
  };

  onChangeCompany = value => {
    this.setState({
      accountLovPrams: {
        companyId: value.key,
      },
    });
  };

  onChangAccount = (value, data, linkage, index) => {
    const { copyDataSource } = this.state;
    linkage.forEach(item => {
      if (item === 'adjustInOpenBank') {
        copyDataSource[index][item] = data.openBankName;
      } else if (item === 'currency') {
        copyDataSource[index][item] = data.currencyCode;
      }
    });
  };

  renderFormItem = (item, index) => {
    switch (item.type) {
      case 'input': {
        if (item.inputDisableEdit) {
          return (
            <Input
              style={{ border: 'none', height: '30px', outlineColor: 'transparent' }}
              disabled
            />
          );
        }
        return <Input style={{ border: 'none', height: '30px', outlineColor: 'transparent' }} />;
      }
      case 'fundCompanyLov': {
        return <FundCompanyLov labelInValue value onChange={this.onChangeCompany} />;
      }
      case 'fundAccountLov': {
        const { accountLovPrams } = this.state;
        return (
          <FundAccountLov
            labelInValue
            value
            accountLovPrams={accountLovPrams}
            onChange={(value, data) => {
              this.onChangAccount(value, data, item.linkage, index);
            }}
          />
        );
      }
      case 'lov': {
        return (
          <Lov
            code={item.lovCode}
            valueKey={item.lovValueKey}
            lableKey={item.lovlabelKey}
            extraParams={item.lovSetOfBooksId}
            single={item.single}
          />
        );
      }
      default: {
        return (
          <Input
            style={{
              border: 'none',
              height: '30px',
              outlineStyle: 'none',
              outlineColor: 'transparent',
            }}
          />
        );
      }
    }
  };

  /**
   * 添加行
   */
  handleAdd = () => {
    const { copyDataSource, count } = this.state;
    const { columns } = this.props;
    const newDataCol = {};
    columns.forEach(item => {
      const { dataIndex } = item;
      newDataCol[dataIndex] = '';
    });
    const newData = {
      ...newDataCol,
      key: count + 1,
    };
    this.setState({
      copyDataSource: [...copyDataSource, newData],
      count: count + 1,
    });
  };

  /**
   * 删除行
   */
  handleDelete = selectedRowKeys => {
    const { copyDataSource } = this.state;
    copyDataSource.splice(selectedRowKeys[0] - 1, 1);
    this.setState({
      copyDataSource,
    });
  };

  /**
   * 保存
   */
  saveTable = () => {
    const { form, saveTable } = this.props;
    const { copyColumns } = this.state;
    const len = copyColumns.filter(item => {
      return item.editable;
    }).length;
    form.validateFields((err, value) => {
      if (!err) {
        const saveValue = this.dealData(value, len);
        saveTable(saveValue);
      }
    });
  };

  /**
   * 处理数据
   */
  dealData = (data, length) => {
    const result = [];
    const resultArr = []; // 创建一个数组
    /**
     * 将对象
      {
        0-paramsCode: "code01"
        0-paramsDesc: "测试COD1212E01sss"
        0-paramsValue: "测试参数值011111"
        1-paramsCode: "wwww"
        1-paramsDesc: "2334"
        1-paramsValue: "44444"
        2-paramsCode: "ssss"
        2-paramsDesc: "sssssss"
        2-paramsValue: "ssssss"
      }
    * 变为对象数组[{name:paramsCode,value:code01},{name:paramsDesc,value:测试COD1212E01sss}]
    */
    for (const key in data) {
      if (Object.prototype.hasOwnProperty.call(data, key)) {
        const temp = {};
        // eslint-disable-next-line
        temp.name = key.split('-')[1];
        temp.value = data[key];
        resultArr.push(temp);
      }
    }
    // 将对象数组等份分割
    const sliceArrayResult = sliceArray(Object.values(resultArr), length);
    /* 将分割后的数组进行遍历重组变为
      [
        {name:value,name:value},
        {name:value,name:value}
      ]
    */
    sliceArrayResult.forEach(item => {
      const tempObj = {};
      item.forEach(otherItem => {
        tempObj[otherItem.name] = otherItem.value;
      });
      result.push(tempObj);
    });
    return result;
  };

  render() {
    const { copyDataSource, copyColumns } = this.state;
    const { pagination, rowSelection, onTableChange, loading } = this.props;
    return (
      <div className="editable">
        <Form>
          <Table
            rowClassName={() => 'editable-row'}
            loading={loading}
            rowSelection={rowSelection}
            scroll={{ x: true }}
            bordered
            dataSource={copyDataSource}
            columns={copyColumns}
            pagination={pagination}
            onChange={onTableChange}
          />
        </Form>
      </div>
    );
  }
}

function map(state) {
  return {
    user: state.user.currentUser,
    company: state.user.company,
  };
}

export default connect(map)(Form.create()(FundEditableTable));
