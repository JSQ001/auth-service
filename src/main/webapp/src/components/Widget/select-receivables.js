import React from 'react';
import { Select, Icon, Popove, Input, Spin } from 'antd';
import ListSelector from 'widget/list-selector';
import 'styles/reimburse/reimburse.scss';
import debounce from 'lodash.debounce';
import config from 'config';
import httpFetch from 'share/httpFetch';
const InputGroup = Input.Group;

class SelectReceivables extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      receivables: [],
      fetching: false,
      showPayee: false,
    };

    this.receivablesSerarch = debounce(this.receivablesSerarch, 500);
  }

  receivablesSerarch = value => {
    const { type, companyId } = this.props;

    if (!value) {
      this.setState({ receivables: [] });
      return;
    }
    this.setState({ fetching: true });

    let url =
      type === 'EMPLOYEE'
        ? `${config.mdataUrl}/api/contact/account/by/name/code?name=${value}`
        : `${
            config.mdataUrl
          }api/vendor/account/by/companyId/name/code?companyId=${companyId}&name=${value}`;

    httpFetch.get(url).then(res => {
      this.setState({
        receivables: res.data,
        fetching: false,
      });
    });
  };

  onSelect = value => {
    this.props.onChange(value);
  };

  showPayee = () => {
    this.setState({ showPayee: true });
  };

  payeeHandleListOk = value => {
    let record = value.result[0];
    if (record) {
      this.props.onChange({ key: record.id, label: record.name });
    } else {
      this.props.onChange({});
    }
    this.setState({ showPayee: false });
  };

  render() {
    const { value, type, disabled, onChange, companyId } = this.props;
    const { showPayee } = this.state;
    return (
      <div className="select-receivables">
        <InputGroup compact>
          <Select
            style={{ width: '84%' }}
            showSearch
            onSearch={this.receivablesSerarch}
            onChange={this.onSelect}
            filterOption={false}
            value={value}
            labelInValue
            defaultActiveFirstOption={false}
            disabled={disabled}
            notFoundContent={this.state.fetching ? <Spin size="small" /> : null}
            showArrow={false}
          >
            {this.state.receivables.map(o => {
              return <Select.Option key={o.id}>{o.name}</Select.Option>;
            })}
          </Select>
          <span onClick={disabled ? () => {} : this.showPayee} className="action">
            <Icon type="ellipsis" />
          </span>
        </InputGroup>
        <ListSelector
          single={true}
          visible={showPayee}
          type={type === 'EMPLOYEE' ? 'select_employee' : 'select_ven'}
          labelKey="name"
          valueKey="id"
          onCancel={() => {
            this.setState({ showPayee: false });
          }}
          onOk={this.payeeHandleListOk}
          extraParams={{ companyId }}
          selectedData={value.key ? [{ id: value.key, name: value.label }] : [{ id: '', name: '' }]}
        />
      </div>
    );
  }
}

export default SelectReceivables;
