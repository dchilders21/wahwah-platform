'use strict';

angular.module('wahwah.amcharts', []);

angular.module('wahwah.amcharts').directive('ngGraph', function () {

    return {
        restrict: 'A',
        scope: {
            graphData: '=',
            graphXKey: '@',
            graphYKeys: '@',
            graphLabels: '@',
            graphTypes: '@',
            graphAxis: '@',
            graphLeftUnit: '@',
            graphRightUnit: '@'
        },
        link: function(scope,element,attrs){

            var graphColors = ["#e55445","#557593", "#63609E"];  //CD731E

            var yKeys = scope.graphYKeys.split(',');
            var labels = scope.graphLabels.split(',');
            var types = scope.graphTypes.split(',');
            var axis = scope.graphAxis.split(',');

            var hasLeftAxis = false;
            var hasRightAxis = false;

            for(var i = 0; i < axis.length; i++){
                if(axis[i] == "left"){
                    hasLeftAxis = true;
                }

                if(axis[i] == "right"){
                    hasRightAxis = true;
                }
            }

            var chart = new AmCharts.AmSerialChart();
            chart.dataProvider = [];
            chart.categoryField = scope.graphXKey;
            chart.categoryAxis.parseDates = true;
            chart.categoryAxis.minPeriod = "DD";
            chart.categoryAxis.axisColor = "#557593";
            chart.chartCursor = new AmCharts.ChartCursor();
            chart.chartCursor.oneBalloonOnly = true;
            chart.chartCursor.categoryBalloonColor = "#557593";
            chart.chartCursor.cursorColor = "#557593";

            var valueAxes = [];

            if(hasLeftAxis){

                var valueAxis = {
                    id: "left",
                    position: "left"
                };

                if(typeof scope.graphLeftUnit != "undefined"){
                    valueAxis.unit = scope.graphLeftUnit;
                    valueAxis.unitPosition = "left";
                }

                valueAxes.push(valueAxis);
            }

            if(hasRightAxis){
                var valueAxis = {
                    id: "right",
                    position: "right"
                };

                if(typeof scope.graphRightUnit != "undefined"){
                    valueAxis.unit = scope.graphRightUnit;
                    valueAxis.unitPosition = "left";
                }

                valueAxes.push(valueAxis);
            }

            chart.valueAxes = valueAxes;

            for(var i = 0; i < yKeys.length; i++){
                var graph = new AmCharts.AmGraph();
                graph.valueField = yKeys[i];
                graph.bullet = "round";
                graph.bulletColor = "#FFFFFF";
                graph.bulletBorderThickness = 2;
                graph.bulletBorderAlpha = 1;
                graph.bulletBorderColor = graphColors[i];
                graph.balloonText = labels[i] + ": [[value]]";

                if(types[i] == "area") {
                    graph.type = "smoothedLine";
                    graph.fillAlphas = 0.5;
                }

                if(types[i] == "line"){
                    graph.type = "smoothedLine";
                }

                if(axis[i] == "left"){
                    graph.valueAxis = "left";
                }

                if(axis[i] == "right"){
                    graph.valueAxis = "right";
                }

                if(graph.valueAxis == "left" && typeof scope.graphLeftUnit != "undefined"){
                    graph.balloonText = labels[i] + ": " + scope.graphLeftUnit + "[[value]]";
                }

                if(graph.valueAxis == "right" && typeof scope.graphRightUnit != "undefined"){
                    graph.balloonText = labels[i] + ": " + scope.graphRightUnit + "[[value]]";
                }


                if(typeof graphColors[i] != "undefined"){
                    graph.lineColor = graphColors[i];
                }

                chart.addGraph(graph);
            }

            chart.write(element.get(0));

            scope.$watch('graphData',function(newValue){
                chart.dataProvider = newValue;
                chart.validateData();
            });
        }
    }

});