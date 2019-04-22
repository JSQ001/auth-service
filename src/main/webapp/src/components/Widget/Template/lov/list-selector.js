import React, { Component } from 'react';
import { Modal, message, Spin } from 'antd';
import config from 'config';
import httpFetch from 'share/httpFetch';
import Lov from './lov';
import { connect } from 'dva';

class ListSelector extends Component {
  constructor(props) {
    super(props);
    this.state = {
      lov: {},
      loading: true,
    };
  }

  componentWillReceiveProps(nextProps) {
    const { visible } = this.props;
    // lov显示
    if (nextProps.visible && !visible) {
      this.getLovByCode(nextProps);
    }
  }

  // 获取lov详情
  getLovByCode = nextProps => {
    const { code, lovData, dispatch } = nextProps;
    if (code) {
      if (code in lovData) {
        this.setState({ lov: { ...lovData[code] }, loading: true }, () => {
          this.setState({ loading: false });
        });
        return;
      }

      this.setState({ loading: true });
      httpFetch
        .get(`${config.baseUrl}/api/lov/detail/${code}`)
        .then(res => {
          console.log(res);
          if (res.data) {
            res.data.columns = res.data.columns.map(o => ({ ...o, title: this.$t(o.title) }));
            res.data.searchForm = res.data.searchForm.map(o => ({ ...o, label: this.$t(o.label) }));
          }
          this.setState({ lov: { ...res.data, key: nextProps.valueKey }, loading: false }, () => {
            const { lov } = this.state;
            dispatch({
              type: 'lov/addLovData',
              payload: { [code]: lov },
            });
          });
        })
        .catch(error => {
          message.error(error.response.data.message);
        });
    }
  };

  onOk = () => {
    const { onOk, single } = this.props;
    if (onOk) {
      if (single) {
        onOk(this.lov.state.selectedRows[0] || {});
      } else {
        onOk(this.lov.state.selectedRows);
      }
    }
  };

  cancelHandle = () => {
    const { onCancel } = this.props;
    if (onCancel) {
      onCancel();
    }
  };

  render() {
    const { loading, lov } = this.state;
    const { visible, width, value } = this.props;
    return (
      <Modal
        title={lov.title}
        visible={visible}
        bodyStyle={{ maxHeight: '65vh', overflow: 'auto', minHeight: '200px' }}
        onOk={this.onOk}
        onCancel={this.cancelHandle}
        width={width || '50vw'}
      >
        {loading ? (
          <Spin />
        ) : (
          <Lov {...this.props} ref={ref => (this.lov = ref)} lov={lov} selectedData={value} />
        )}
      </Modal>
    );
  }
}

function mapStateToProps(state) {
  return {
    lovData: state.lov.data,
  };
}

export default connect(mapStateToProps)(ListSelector);
