import http from '@/util/http'

export const stockList = (params, data) => http('/stock', params, data, 'GET')

export const stockHistory = (params, data) => http('/stock/history/{code}', params, data, 'GET')

export const backTestStrategy = (params, data) => http('/stock_back_test/strategy', params, data, 'GET')

export const backTestStrategyParams = (params, data) => http('/stock_back_test/strategy/params', params, data, 'GET')

export const backTest = (params, data) => http('/stock_back_test', params, data, 'POST')

export const stockStrategyHistory = (params, data) => http('/stock_search_best/history', params, data, 'GET')

export const stockStrategyHistoryById = (params, data) => http('/stock_search_best/history/{id}', params, data, 'GET')

export const runSearchBest = (params, data) => http('/stock_search_best/run', params, data, 'POST')

export const reRunSearchBest = (params, data) => http('/stock_search_best/re_run', params, data, 'POST')

export const runResultList = (params, data) => http('/stock_search_best/result', params, data, 'GET')

export const runResultDetail = (params, data) => http('/stock_search_best/result/detail', params, data, 'GET')

export const bestParamsList = (params, data) => http('/stock_search_best/param', params, data, 'GET')

export const findBestParams = (params, data) => http('/stock_search_best/param/run', params, data, 'POST')

export const suggestLast = (params, data) => http('/stock_simulation/suggest', params, data, 'POST')

export const simulation = (params, data) => http('/stock_simulation/simulation', params, data, 'POST')

export const stockPotentialList = (params, data) => http('/stock/potential', params, data, 'GET')

export const stockIndicatorList = (params, data) => http('/stock/indicator', params, data, 'GET')

export const reloadIndicator = (params, data) => http('/stock/indicator/reload', params, data, 'POST')
